package com.badou.project.gpucalc.precache;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 增强版大模型显存估算引擎（带详细打印和优化）
 */
public class EnhancedVramEstimator {


    // KV缓存管理因子映射表（基于序列长度）
    private static final Map<Integer, Double> KV_FACTOR_MAP = createKvFactorMap();

    /**
     * 创建KV缓存管理因子映射表
     */
    private static Map<Integer, Double> createKvFactorMap() {
        Map<Integer, Double> map = new HashMap<>();
        map.put(1024, 1.5);
        map.put(2048, 1.8);
        map.put(3072, 2.0);
        map.put(4096, 2.2);
        map.put(6144, 2.5);
        map.put(8192, 2.8);
        map.put(12288, 3.2);
        map.put(16384, 3.6);
        map.put(24576, 4.2);
        map.put(32768, 4.8);
        map.put(40000, 5.2);
        return map;
    }

    /**
     * 显存估算核心方法（带详细打印）
     */
    public static double estimateWithLog(
            double modelSizeB,       // 模型大小（十亿）
            int numLayers,           // 层数
            int hiddenSize,          // 隐藏层大小
            int numAttentionHeads,   // 注意力头数
            int numKVHeads,          // KV头数（GQA）
            int maxModelLen,         // 最大模型长度（单序列最大token数）
            int maxNumBatchedTokens, // 批次最大token数
            int tpSize,              // 张量并行数
            double gpuMem,           // GPU总显存(GiB)
            double utilization,      // 利用率
            boolean isVL,            // 是否多模态
            int imageCount           // 图像数量
    ) {
        System.out.println("\n===== 开始显存估算 =====");
        System.out.printf("输入参数: modelSizeB=%.1fB, numLayers=%d, hiddenSize=%d\n",
                modelSizeB, numLayers, hiddenSize);
        System.out.printf("         numAttentionHeads=%d, numKVHeads=%d\n",
                numAttentionHeads, numKVHeads);
        System.out.printf("         maxModelLen=%d, maxNumBatchedTokens=%d, tpSize=%d\n",
                maxModelLen, maxNumBatchedTokens, tpSize);
        System.out.printf("         gpuMem=%.1fGiB, utilization=%.2f, isVL=%b, imageCount=%d\n",
                gpuMem, utilization, isVL, imageCount);

        // 1. 模型权重（精确）
        double weights = calculateModelWeights(modelSizeB, numLayers, hiddenSize, tpSize);
        System.out.printf("\n[权重计算] 模型权重: %.2f GiB\n", weights);

        // 2. KV缓存（整合批次大小和GQA）
        double kvCache = calculateKvCache(
                numLayers, hiddenSize, numAttentionHeads, numKVHeads,
                maxModelLen, maxNumBatchedTokens, tpSize
        );
        System.out.printf("[KV缓存] KV缓存: %.2f GiB\n", kvCache);

        // 3. 运行时开销
        double runtime = calculateRuntime(modelSizeB, isVL, imageCount);
        System.out.printf("[运行时] 运行时开销: %.2f GiB\n", runtime);

        // 4. 系统级开销
        double system = calculateSystemOverhead(gpuMem);
        System.out.printf("[系统开销] 系统级开销: %.2f GiB\n", system);

        double total = weights + kvCache + runtime + system;
        System.out.printf("\n[总计] 预估显存需求: %.2f GiB\n", total);

        return total;
    }

    /**
     * 模型权重计算（带详细打印）
     */
    private static double calculateModelWeights(double modelSizeB, int numLayers,
                                                int hiddenSize, int tpSize) {
        System.out.println("\n--- 模型权重计算 ---");
        System.out.printf("输入参数: modelSizeB=%.1fB, numLayers=%d, hiddenSize=%d, tpSize=%d\n",
                modelSizeB, numLayers, hiddenSize, tpSize);

        // 基础权重 = 参数量 * 数据类型大小 / TP数
        double baseWeights = (modelSizeB * 1e9 * 2) / (1024 * 1024 * 1024) / tpSize;
        System.out.printf("基础权重: %.2f GiB (未考虑额外开销)\n", baseWeights);

        // 额外开销因子（层归一化、偏置等）
        double extraFactor = 1.07; // 默认7%额外开销

        // 小模型额外开销更高
        if (modelSizeB < 10) {
            extraFactor = 1.09; // 9%额外开销
            System.out.println("小模型优化: 额外开销因子=1.09");
        } else {
            System.out.println("标准模型: 额外开销因子=1.07");
        }

        double weights = baseWeights * extraFactor;
        System.out.printf("最终权重: %.2f GiB (含%.0f%%额外开销)\n",
                weights, (extraFactor - 1) * 100);

        return weights;
    }

    /**
     * KV缓存计算（带详细打印，支持GQA）
     */
    private static double calculateKvCache(
            int numLayers, int hiddenSize, int numAttentionHeads, int numKVHeads,
            int maxModelLen, int maxNumBatchedTokens, int tpSize) {

        System.out.println("\n--- KV缓存计算 ---");
        System.out.printf("输入参数: numLayers=%d, hiddenSize=%d, numAttentionHeads=%d, numKVHeads=%d\n",
                numLayers, hiddenSize, numAttentionHeads, numKVHeads);
        System.out.printf("         maxModelLen=%d, maxNumBatchedTokens=%d, tpSize=%d\n",
                maxModelLen, maxNumBatchedTokens, tpSize);

        // 计算每个注意力头的维度
        double headDim = hiddenSize / (double) numAttentionHeads;
        System.out.printf("每个注意力头的维度: %.2f\n", headDim);

        // 每token的KV缓存大小 (字节)
        // 注意：对于GQA模型，KV头数可能小于注意力头数
        double perTokenBytes = 2 * numLayers * headDim * numKVHeads * 2;
        System.out.printf("每token KV缓存大小: %.2f bytes\n", perTokenBytes);

        // 计算有效序列长度（考虑批次大小）
        double effectiveSeqLen = calculateEffectiveSeqLen(maxModelLen, maxNumBatchedTokens);
        System.out.printf("有效序列长度: %.2f (考虑批次大小)\n", effectiveSeqLen);

        // KV缓存 = (每token大小 * 有效序列长度) / TP数
        double kvCache = (perTokenBytes * effectiveSeqLen) / (1024 * 1024 * 1024) / tpSize;
        System.out.printf("基础KV缓存: %.2f GiB\n", kvCache);

        // 日志校准因子（基于实际日志值）
        double calibrationFactor = getCalibrationFactor(maxModelLen);
        System.out.printf("日志校准因子: %.2f (基于序列长度%d)\n", calibrationFactor, maxModelLen);

        double finalKvCache = kvCache * calibrationFactor;
        System.out.printf("最终KV缓存: %.2f GiB\n", finalKvCache);

        return finalKvCache;
    }

    /**
     * 计算有效序列长度（带详细打印）
     */
    private static double calculateEffectiveSeqLen(int maxModelLen, int maxNumBatchedTokens) {
        System.out.println("\n--- 有效序列长度计算 ---");
        System.out.printf("输入参数: maxModelLen=%d, maxNumBatchedTokens=%d\n",
                maxModelLen, maxNumBatchedTokens);

        // 基础有效长度 = 最大模型长度
        double effectiveLen = maxModelLen;
        System.out.printf("基础有效长度: %.2f\n", effectiveLen);

        // 如果批次大小大于模型长度，增加批次因子
        if (maxNumBatchedTokens > maxModelLen) {
            double batchFactor = Math.sqrt(maxNumBatchedTokens / (double) maxModelLen);
            System.out.printf("批次因子: %.2f (maxNumBatchedTokens > maxModelLen)\n", batchFactor);
            effectiveLen *= batchFactor;
            System.out.printf("应用批次因子后: %.2f\n", effectiveLen);
        }

        // 长序列优化（>8192）
        if (maxModelLen > 8192) {
            double longSeqFactor = 0.85;
            System.out.printf("长序列优化因子: %.2f (maxModelLen > 8192)\n", longSeqFactor);
            effectiveLen *= longSeqFactor;
            System.out.printf("应用长序列优化后: %.2f\n", effectiveLen);
        }

        System.out.printf("最终有效序列长度: %.2f\n", effectiveLen);
        return effectiveLen;
    }

    /**
     * 日志校准因子（基于实际日志值）
     */
    private static double getCalibrationFactor(int maxModelLen) {
        // 基于实际日志值的校准
        if (maxModelLen <= 2048) {
            return 1.0;
        } else if (maxModelLen <= 4096) {
            return 1.2;
        } else if (maxModelLen <= 8192) {
            return 1.5;
        } else if (maxModelLen <= 16384) {
            return 2.0;
        } else if (maxModelLen <= 32768) {
            return 2.8;
        } else {
            return 3.5; // 40000序列长度
        }
    }

    /**
     * 运行时开销计算（带详细打印）
     */
    private static double calculateRuntime(double modelSizeB, boolean isVL, int imageCount) {
        System.out.println("\n--- 运行时开销计算 ---");
        System.out.printf("输入参数: modelSizeB=%.1fB, isVL=%b, imageCount=%d\n",
                modelSizeB, isVL, imageCount);

        // 基础运行时开销
        double baseRuntime = 0.6;
        System.out.printf("基础运行时开销: %.2f GiB\n", baseRuntime);

        // 模型规模相关开销
        double modelSizeOverhead = modelSizeB * 0.08;
        System.out.printf("模型规模开销: %.2f GiB (每B参数0.08GiB)\n", modelSizeOverhead);

        double runtime = baseRuntime + modelSizeOverhead;
        System.out.printf("基础运行时总计: %.2f GiB\n", runtime);

        // 多模态额外开销
        if (isVL) {
            double visionBase = 0.8; // 基础视觉开销
            double perImage = 0.3;   // 每张图像增加0.3GiB
            double visionOverhead = visionBase + imageCount * perImage;

            System.out.printf("多模态开销: 基础视觉开销=%.2f GiB\n", visionBase);
            System.out.printf("          每张图像开销=%.2f GiB * %d = %.2f GiB\n",
                    perImage, imageCount, imageCount * perImage);
            System.out.printf("          总视觉开销=%.2f GiB\n", visionOverhead);

            runtime += visionOverhead;
            System.out.printf("包含多模态开销后: %.2f GiB\n", runtime);
        }

        System.out.printf("最终运行时开销: %.2f GiB\n", runtime);
        return runtime;
    }

    /**
     * 系统级开销计算（带详细打印）
     */
    private static double calculateSystemOverhead(double gpuMem) {
        System.out.println("\n--- 系统级开销计算 ---");
        System.out.printf("输入参数: gpuMem=%.1fGiB\n", gpuMem);

        double system;
        if (gpuMem <= 16) {
            system = 0.8;
            System.out.println("小显存卡(<=16GB): 系统开销=0.8 GiB");
        } else if (gpuMem <= 24) {
            system = 0.6;
            System.out.println("中显存卡(<=24GB): 系统开销=0.6 GiB");
        } else {
            system = 0.4;
            System.out.println("大显存卡(>24GB): 系统开销=0.4 GiB");
        }

        return system;
    }

    /**
     * 显存需求检查
     */
    public static boolean checkMemoryRequirement(double estimatedVram, double availableVram) {
        System.out.println("\n--- 显存需求检查 ---");
        System.out.printf("预估显存: %.2f GiB, 可用显存: %.2f GiB\n", estimatedVram, availableVram);

        boolean meetsRequirement = estimatedVram <= availableVram * 0.95;
        System.out.printf("需求检查结果: %s\n", meetsRequirement ? "通过" : "不通过");

        return meetsRequirement;
    }

    /**
     * 生成优化建议（带详细打印）
     */
    public static Map<String, String> generateOptimizationSuggestions(
            double modelSizeB, int maxModelLen, int maxNumBatchedTokens,
            int tpSize, boolean isVL, int imageCount,
            double estimatedVram, double availableVram) {

        System.out.println("\n--- 生成优化建议 ---");
        System.out.printf("输入参数: modelSizeB=%.1fB, maxModelLen=%d, maxNumBatchedTokens=%d\n",
                modelSizeB, maxModelLen, maxNumBatchedTokens);
        System.out.printf("         tpSize=%d, isVL=%b, imageCount=%d\n",
                tpSize, isVL, imageCount);
        System.out.printf("         estimatedVram=%.2fGiB, availableVram=%.2fGiB\n",
                estimatedVram, availableVram);

        Map<String, String> suggestions = new HashMap<>();

        // 超出显存警告
        if (estimatedVram > availableVram) {
            String warning = String.format(
                    "预估显存需求%.2fGiB超过可用显存%.2fGiB",
                    estimatedVram, availableVram
            );
            suggestions.put("WARNING", warning);
            System.out.println(" - WARNING: " + warning);
        }

        // 基础配置信息
        suggestions.put("TP_SIZE", "当前张量并行数: " + tpSize);
        suggestions.put("MAX_MODEL_LEN", "当前最大模型长度: " + maxModelLen);
        suggestions.put("MAX_BATCHED_TOKENS", "当前批次最大token数: " + maxNumBatchedTokens);

        System.out.println(" - TP_SIZE: " + suggestions.get("TP_SIZE"));
        System.out.println(" - MAX_MODEL_LEN: " + suggestions.get("MAX_MODEL_LEN"));
        System.out.println(" - MAX_BATCHED_TOKENS: " + suggestions.get("MAX_BATCHED_TOKENS"));

        // 多模态模型特定建议
        if (isVL) {
            // 图像数量建议
            int maxImages = (int) Math.max(1, (availableVram - 12) / 0.8);
            suggestions.put("IMAGE_COUNT", "建议最多图像数: " + maxImages);

            // 视觉编码器优化
            suggestions.put("VISION_TIP", "考虑使用--disable-mm-preprocessor-cache减少视觉预处理开销");

            System.out.println(" - IMAGE_COUNT: " + suggestions.get("IMAGE_COUNT"));
            System.out.println(" - VISION_TIP: " + suggestions.get("VISION_TIP"));
        }

        // 序列长度优化建议
        if (estimatedVram > availableVram * 0.8) {
            int recommendedModelLen = Math.max(4096, maxModelLen / 2);
            suggestions.put("MODEL_LEN_TIP", "减少max-model-len可显著降低显存，建议尝试: " + recommendedModelLen);

            int recommendedBatchTokens = Math.max(2048, maxNumBatchedTokens / 2);
            suggestions.put("BATCH_TOKENS_TIP", "减少max-num-batched-tokens可降低显存，建议尝试: " + recommendedBatchTokens);

            System.out.println(" - MODEL_LEN_TIP: " + suggestions.get("MODEL_LEN_TIP"));
            System.out.println(" - BATCH_TOKENS_TIP: " + suggestions.get("BATCH_TOKENS_TIP"));
        }

        // TP优化建议
        if (modelSizeB > 20 && tpSize < 4) {
            suggestions.put("TP_TIP", "增加张量并行数可降低单卡显存需求，建议尝试TP=" + (tpSize * 2));
            System.out.println(" - TP_TIP: " + suggestions.get("TP_TIP"));
        }

        return suggestions;
    }

    /**
     * 从模型名称提取参数规模（带详细打印）
     */
    public static double parseModelSize(String modelName) {
        System.out.println("\n--- 模型名称解析 ---");
        System.out.printf("输入模型名称: %s\n", modelName);

        // 使用正则表达式匹配模型名称中的数字和B后缀
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)(?:\\.\\d+)?[bB]");
        java.util.regex.Matcher matcher = pattern.matcher(modelName);

        if (matcher.find()) {
            double size = Double.parseDouble(matcher.group(1));
            System.out.printf("成功提取参数规模: %.1fB\n", size);
            return size;
        }

        // 尝试匹配纯数字
        pattern = java.util.regex.Pattern.compile("(\\d+)(?:\\.\\d+)?");
        matcher = pattern.matcher(modelName);

        if (matcher.find()) {
            double size = Double.parseDouble(matcher.group(1));
            System.out.printf("成功提取参数规模: %.1fB\n", size);
            return size;
        }

        throw new IllegalArgumentException("无法从模型名称提取参数规模: " + modelName);
    }
}