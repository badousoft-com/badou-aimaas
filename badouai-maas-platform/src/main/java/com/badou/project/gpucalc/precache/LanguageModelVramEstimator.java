package com.badou.project.gpucalc.precache;

/**
 * 高精度显存估算引擎（调试增强版）
 * 核心特性：
 * 1. 详细打印每一步计算参数和结果
 * 2. 支持多显卡容量（16G/24G/40G）
 * 3. 多模型参数组合测试
 * 4. 极端场景覆盖测试
 */
public class LanguageModelVramEstimator {

    private enum ModelScale {
        SMALL, MEDIUM, LARGE
    }

    public static double estimateWithLog(
            double modelSizeB, int numLayers, int hiddenSize,
            int numKVHeads, int maxModelLen, int maxNumBatchedTokens,
            int tpSize, int blockSize, double gpuMem,
            double utilization, boolean enforceEager) {

        // ===== 初始化打印 =====
        System.out.println("\n===== 显存估算引擎 v8.0 (调试增强版) =====");
        System.out.printf("模型: %.1fB | 层数: %d | 隐藏层: %d | KV头数: %d\n",
                modelSizeB, numLayers, hiddenSize, numKVHeads);
        System.out.printf("序列: %d | 批次Token: %d | 块大小: %d | GPU显存: %.1fGiB\n",
                maxModelLen, maxNumBatchedTokens, blockSize, gpuMem);
        System.out.printf("并行数: %d | 利用率: %.2f | Eager模式: %b\n",
                tpSize, utilization, enforceEager);

        // ===== 模型规模分类 =====
        ModelScale scale = getModelScale(modelSizeB);
        System.out.printf("\n[模型分类] %s (%.1fB)\n", scale.name(), modelSizeB);

        // ===== 1. 模型权重计算 =====
        double weights = calculateModelWeights(modelSizeB, tpSize, scale);

        // ===== 2. KV缓存计算 =====
        double kvCache = calculateKvCache(
                numLayers, hiddenSize, numKVHeads,
                maxModelLen, blockSize, tpSize, scale
        );

        // ===== 3. 运行时开销 =====
        double runtime = calculateRuntime(modelSizeB, enforceEager, scale);

        // ===== 4. 多卡通信开销 =====
        double tpOverhead = calculateTpOverhead(tpSize, scale);

        // ===== 显存需求汇总 =====
        double perCardBase = weights + kvCache + runtime + tpOverhead;
        double perCardAdjusted = perCardBase * utilization;
        double clusterTotal = perCardAdjusted * tpSize;

        // ===== 详细分析报告 =====
        printDetailedAnalysis(
                weights, kvCache, runtime, tpOverhead,
                perCardBase, perCardAdjusted, clusterTotal,
                tpSize, utilization, gpuMem
        );

        return perCardAdjusted;
    }

    // --- 核心计算逻辑（带详细参数打印）---

    private static double calculateModelWeights(double modelSizeB, int tpSize, ModelScale scale) {
        System.out.println("\n--- 模型权重计算 ---");
        System.out.printf("输入参数: modelSizeB=%.1fB, tpSize=%d, scale=%s\n", modelSizeB, tpSize, scale.name());

        double baseWeights = (modelSizeB * 1e9 * 2) / (1024 * 1024 * 1024) / tpSize;
        System.out.printf("基础权重 = (%.1fB × 10^9 × 2字节/参数) / %d = %.2f GiB\n",
                modelSizeB, tpSize, baseWeights);

        double extraFactor;
        switch (scale) {
            case SMALL: extraFactor = 1.09; break;
            case MEDIUM: extraFactor = 1.07; break;
            case LARGE: extraFactor = 1.05; break;
            default: extraFactor = 1.05;
        }
        System.out.printf("额外开销因子: %.3f (%s模型)\n", extraFactor, scale.name());

        double weights = baseWeights * extraFactor;
        System.out.printf("最终权重 = %.2f × %.3f = %.2f GiB\n", baseWeights, extraFactor, weights);

        return weights;
    }

    private static double calculateKvCache(
            int numLayers, int hiddenSize, int numKVHeads,
            int maxModelLen, int blockSize, int tpSize,
            ModelScale scale) {

        System.out.println("\n--- KV缓存计算 ---");
        System.out.printf("输入参数: numLayers=%d, hiddenSize=%d, numKVHeads=%d\n",
                numLayers, hiddenSize, numKVHeads);
        System.out.printf("         maxModelLen=%d, blockSize=%d, tpSize=%d, scale=%s\n",
                maxModelLen, blockSize, tpSize, scale.name());

        double headDim = hiddenSize / (double) numKVHeads;
        System.out.printf("头维度 = %d / %d = %.2f\n", hiddenSize, numKVHeads, headDim);

        double perTokenBytes = 2 * numLayers * numKVHeads * headDim;
        System.out.printf("每token字节数 = 2 × %d × %d × %.2f = %.0f bytes\n",
                numLayers, numKVHeads, headDim, perTokenBytes);

        int blocksNeeded = (int) Math.ceil(maxModelLen / (double) blockSize);
        System.out.printf("块需求 = ceil(%d / %d) = %d 块\n", maxModelLen, blockSize, blocksNeeded);

        double fragmentationFactor = 1.0 + 0.12 * (16.0 / blockSize);
        System.out.printf("碎片因子 = 1.0 + 0.12 × (16 / %d) = %.3f\n", blockSize, fragmentationFactor);

        double blockMetadata = blocksNeeded * (0.4 / (1024.0 * 1024.0 * 1024.0));
        System.out.printf("块元数据 = %d × 0.4KB = %.6f GiB\n", blocksNeeded, blockMetadata);

        double kvCacheBase = (perTokenBytes * blocksNeeded * fragmentationFactor) / (1024 * 1024 * 1024);
        System.out.printf("基础KV缓存 = (%.0f × %d × %.3f) / (1024^3) = %.2f GiB\n",
                perTokenBytes, blocksNeeded, fragmentationFactor, kvCacheBase);

        double kvCacheTotal = kvCacheBase + blockMetadata;
        System.out.printf("总KV缓存 = %.2f + %.6f = %.2f GiB\n", kvCacheBase, blockMetadata, kvCacheTotal);

        double kvCachePerCard = kvCacheTotal / tpSize;
        System.out.printf("单卡KV缓存 = %.2f / %d = %.2f GiB\n", kvCacheTotal, tpSize, kvCachePerCard);

        return kvCachePerCard;
    }

    private static double calculateRuntime(double modelSizeB, boolean enforceEager, ModelScale scale) {
        System.out.println("\n--- 运行时开销计算 ---");
        System.out.printf("输入参数: modelSizeB=%.1fB, enforceEager=%b, scale=%s\n",
                modelSizeB, enforceEager, scale.name());

        double baseRuntime = 0.10;
        System.out.printf("基础运行时开销 = %.2f GiB\n", baseRuntime);

        double activationFactor;
        switch (scale) {
            case SMALL: activationFactor = 0.65; break;
            case MEDIUM: activationFactor = 0.25; break;
            case LARGE: activationFactor = 0.17; break;
            default: activationFactor = 0.25;
        }
        System.out.printf("激活内存因子 = %.2f (%s模型)\n", activationFactor, scale.name());

        double activationMemory = modelSizeB * activationFactor;
        System.out.printf("激活内存 = %.1fB × %.2f = %.2f GiB\n", modelSizeB, activationFactor, activationMemory);

        double runtime = baseRuntime + activationMemory;
        System.out.printf("基础运行时 = %.2f + %.2f = %.2f GiB\n", baseRuntime, activationMemory, runtime);

        if (!enforceEager) {
            runtime -= 0.21;
            System.out.printf("CUDA图优化节省 = 0.21 GiB\n");
            System.out.printf("最终运行时 = %.2f - 0.21 = %.2f GiB\n", runtime + 0.21, runtime);
        } else {
            System.out.printf("最终运行时 = %.2f GiB (eager模式无优化)\n", runtime);
        }

        return runtime;
    }

    private static double calculateTpOverhead(int tpSize, ModelScale scale) {
        System.out.println("\n--- 多卡通信开销 ---");
        System.out.printf("输入参数: tpSize=%d, scale=%s\n", tpSize, scale.name());

        if (tpSize == 1) {
            System.out.println("TP=1，无多卡通信开销");
            return 0.0;
        }

        double baseOverhead = 0.15 * Math.log(tpSize);
        System.out.printf("基础开销 = 0.15 × ln(%d) = %.3f GiB\n", tpSize, baseOverhead);

        double scaleFactor;
        switch (scale) {
            case LARGE: scaleFactor = 1.6; break;
            case MEDIUM: scaleFactor = 1.2; break;
            default: scaleFactor = 1.0;
        }
        System.out.printf("规模因子 = %s模型 => %.1f\n", scale.name(), scaleFactor);

        double overhead = baseOverhead * scaleFactor;
        System.out.printf("总开销 = %.3f × %.1f = %.3f GiB\n", baseOverhead, scaleFactor, overhead);

        return overhead;
    }

    // --- 分析功能 ---

    private static void printDetailedAnalysis(
            double weights, double kvCache, double runtime, double tpOverhead,
            double perCardBase, double perCardAdjusted, double clusterTotal,
            int tpSize, double utilization, double gpuMem) {

        System.out.println("\n\n--- 显存详细分析 ---");
        System.out.println("| 组件                | 单卡显存 (GiB) | 占比    |");
        System.out.println("|---------------------|----------------|---------|");
        System.out.printf("| 模型权重            | %-14.2f | %5.1f%% |\n", weights, weights/perCardBase*100);
        System.out.printf("| KV缓存              | %-14.2f | %5.1f%% |\n", kvCache, kvCache/perCardBase*100);
        System.out.printf("| 运行时开销          | %-14.2f | %5.1f%% |\n", runtime, runtime/perCardBase*100);
        System.out.printf("| 多卡通信开销        | %-14.2f | %5.1f%% |\n", tpOverhead, tpOverhead/perCardBase*100);
        System.out.println("|---------------------|----------------|---------|");
        System.out.printf("| 单卡基础需求        | %-14.2f | 100.0%% |\n", perCardBase);
        System.out.printf("| 应用利用率(%.2f)    | %-14.2f |         |\n", utilization, perCardAdjusted);
        System.out.println("|---------------------|----------------|---------|");
        System.out.printf("| %d卡集群总需求       | %-14.2f |         |\n", tpSize, clusterTotal);

        // 显存容量对比
        System.out.println("\n--- 显存容量对比 ---");
        System.out.printf("GPU显存容量: %.1f GiB\n", gpuMem);
        System.out.printf("预估单卡需求: %.2f GiB (%.1f%%利用率)\n", perCardAdjusted, utilization*100);

        if (perCardAdjusted > gpuMem) {
            double deficit = perCardAdjusted - gpuMem;
            System.out.printf("❌ 显存不足: 超出%.2f GiB (%.1f%%)\n", deficit, deficit/gpuMem*100);
        } else {
            double margin = gpuMem - perCardAdjusted;
            System.out.printf("✅ 显存充足: 余量%.2f GiB (%.1f%%)\n", margin, margin/gpuMem*100);
        }
    }

    // --- 辅助函数 ---

    private static ModelScale getModelScale(double modelSizeB) {
        if (modelSizeB < 10) return ModelScale.SMALL;
        if (modelSizeB < 20) return ModelScale.MEDIUM;
        return ModelScale.LARGE;
    }

    // ========================= 多场景测试用例 =========================

    /**
     * 测试案例1: Qwen2.5-7B (P100 16GB)
     * 场景: 小模型+长序列+低显存
     */
    public static void testQwen7B_P100() {
        System.out.println("\n\n===== 测试案例1: Qwen2.5-7B (P100 16GB) =====");
        estimateWithLog(
                7.0,    // 7B模型
                28,     // 层数
                3584,   // 隐藏层
                4,      // KV头数
                40000,  // 序列长度
                2048,   // 批次token
                1,      // TP数
                16,     // block_size
                16.0,   // GPU显存(P100)
                0.95,   // 利用率
                true    // eager模式
        );
    }

    /**
     * 测试案例2: Qwen2.5-14B (RTX4090 24GB)
     * 场景: 中模型+中序列+高效显卡
     */
    public static void testQwen14B_RTX4090() {
        System.out.println("\n\n===== 测试案例2: Qwen2.5-14B (RTX4090 24GB) =====");
        estimateWithLog(
                14.0,   // 14B模型
                32,     // 层数
                4096,   // 隐藏层
                4,      // KV头数
                16384,  // 序列长度
                4096,   // 批次token
                1,      // TP数
                32,     // block_size
                24.0,   // GPU显存(RTX4090)
                0.98,   // 利用率
                false   // 非eager模式
        );
    }

    /**
     * 测试案例3: Qwen3-32B (A100 40GB)
     * 场景: 大模型+短序列+高效显卡
     */
    public static void testQwen32B_A100() {
        System.out.println("\n\n===== 测试案例3: Qwen3-32B (A100 40GB) =====");
        estimateWithLog(
                32.0,   // 32B模型
                64,     // 层数
                5120,   // 隐藏层
                8,      // KV头数
                8192,   // 序列长度
                8192,   // 批次token
                1,      // TP数
                8,      // block_size
                40.0,   // GPU显存(A100)
                0.95,   // 利用率
                false   // 非eager模式
        );
    }

    /**
     * 测试案例4: 极端场景测试 (7B模型+小显存)
     * 场景: 小模型+长序列+低显存+高利用率
     */
    public static void testExtremeScenario() {
        System.out.println("\n\n===== 测试案例4: 极端场景测试 =====");
        estimateWithLog(
                7.0,    // 7B模型
                28,     // 层数
                3584,   // 隐藏层
                4,      // KV头数
                40000,  // 序列长度
                2048,   // 批次token
                1,      // TP数
                16,     // block_size
                12.0,   // GPU显存(12GB)
                0.98,   // 利用率
                true    // eager模式
        );
    }

    /**
     * 测试案例5: 多卡部署测试 (32B模型+4卡)
     * 场景: 大模型+长序列+多卡并行
     */
    public static void testMultiGPUDeployment() {
        System.out.println("\n\n===== 测试案例5: 多卡部署测试 =====");
        estimateWithLog(
                32.0,   // 32B模型
                64,     // 层数
                5120,   // 隐藏层
                8,      // KV头数
                40000,  // 序列长度
                8192,   // 批次token
                4,      // TP数
                16,     // block_size
                24.0,   // GPU显存(24GB/卡)
                0.95,   // 利用率
                false   // 非eager模式
        );
    }

    /**
     * 测试案例6: 小批次优化测试 (14B模型+小批次)
     * 场景: 中模型+小批次+不同块大小
     */
    public static void testSmallBatchOptimization() {
        System.out.println("\n\n===== 测试案例6: 小批次优化测试 =====");
        System.out.println("--- 块大小=16 ---");
        estimateWithLog(
                14.0,   // 14B模型
                32,     // 层数
                4096,   // 隐藏层
                4,      // KV头数
                16384,  // 序列长度
                512,    // 批次token
                1,      // TP数
                16,     // block_size
                24.0,   // GPU显存
                0.95,   // 利用率
                true    // eager模式
        );

        System.out.println("\n--- 块大小=8 ---");
        estimateWithLog(
                14.0,   // 14B模型
                32,     // 层数
                4096,   // 隐藏层
                4,      // KV头数
                16384,  // 序列长度
                512,    // 批次token
                1,      // TP数
                8,      // block_size
                24.0,   // GPU显存
                0.95,   // 利用率
                true    // eager模式
        );
    }

    /**
     * 测试案例7: 利用率影响测试 (32B模型+不同利用率)
     * 场景: 大模型+不同利用率设置
     */
    public static void testUtilizationImpact() {
        System.out.println("\n\n===== 测试案例7: 利用率影响测试 =====");
        System.out.println("--- 利用率=0.90 ---");
        estimateWithLog(
                32.0,   // 32B模型
                64,     // 层数
                5120,   // 隐藏层
                8,      // KV头数
                16384,  // 序列长度
                4096,   // 批次token
                1,      // TP数
                16,     // block_size
                24.0,   // GPU显存
                0.90,   // 利用率
                false   // 非eager模式
        );

        System.out.println("\n--- 利用率=0.98 ---");
        estimateWithLog(
                32.0,   // 32B模型
                64,     // 层数
                5120,   // 隐藏层
                8,      // KV头数
                16384,  // 序列长度
                4096,   // 批次token
                1,      // TP数
                16,     // block_size
                24.0,   // GPU显存
                0.98,   // 利用率
                false   // 非eager模式
        );
    }

    // ========================= P100 16GB显卡测试用例 =========================

    /**
     * 测试案例8: P100 16GB - 7B模型短序列
     * 场景: 小模型+短序列+单卡
     */
    public static void testP100_7B_ShortSeq() {
        System.out.println("\n\n===== 测试案例8: P100 16GB - 7B模型短序列 =====");
        estimateWithLog(
                7.0,    // 7B模型
                28,     // 层数
                3584,   // 隐藏层
                4,      // KV头数
                2048,   // 序列长度
                512,    // 批次token
                1,      // TP数
                16,     // block_size
                16.0,   // GPU显存(P100)
                0.95,   // 利用率
                true    // eager模式
        );
    }

    /**
     * 测试案例9: P100 16GB - 7B模型长序列
     * 场景: 小模型+长序列+块优化
     */
    public static void testP100_7B_LongSeq() {
        System.out.println("\n\n===== 测试案例9: P100 16GB - 7B模型长序列 =====");
        estimateWithLog(
                7.0,    // 7B模型
                28,     // 层数
                3584,   // 隐藏层
                4,      // KV头数
                32768,  // 序列长度
                1024,   // 批次token
                1,      // TP数
                32,     // block_size(优化长序列)
                16.0,   // GPU显存(P100)
                0.98,   // 利用率
                false   // 非eager模式
        );
    }

    /**
     * 测试案例10: P100 16GB - 14B模型极限测试
     * 场景: 中模型+低显存+高利用率
     */
    public static void testP100_14B_Extreme() {
        System.out.println("\n\n===== 测试案例10: P100 16GB - 14B模型极限测试 =====");
        estimateWithLog(
                14.0,   // 14B模型
                32,     // 层数
                4096,   // 隐藏层
                4,      // KV头数
                8192,   // 序列长度
                512,    // 批次token
                1,      // TP数
                8,      // block_size(小尺寸优化)
                16.0,   // GPU显存(P100)
                0.98,   // 利用率
                false   // 非eager模式
        );
    }

    // ========================= 2080Ti 22GB显卡测试用例 =========================

    /**
     * 测试案例11: 2080Ti 22GB - 7B模型高吞吐
     * 场景: 小模型+大批次+高利用率
     */
    public static void test2080Ti_7B_HighThroughput() {
        System.out.println("\n\n===== 测试案例11: 2080Ti 22GB - 7B模型高吞吐 =====");
        estimateWithLog(
                7.0,    // 7B模型
                28,     // 层数
                3584,   // 隐藏层
                4,      // KV头数
                4096,   // 序列长度
                8192,   // 批次token
                1,      // TP数
                16,     // block_size
                22.0,   // GPU显存(2080Ti)
                0.98,   // 利用率
                false   // 非eager模式
        );
    }

    /**
     * 测试案例12: 2080Ti 22GB - 14B模型标准部署
     * 场景: 中模型+中序列+单卡
     */
    public static void test2080Ti_14B_Standard() {
        System.out.println("\n\n===== 测试案例12: 2080Ti 22GB - 14B模型标准部署 =====");
        estimateWithLog(
                14.0,   // 14B模型
                32,     // 层数
                4096,   // 隐藏层
                4,      // KV头数
                16384,  // 序列长度
                2048,   // 批次token
                1,      // TP数
                16,     // block_size
                22.0,   // GPU显存(2080Ti)
                0.95,   // 利用率
                false   // 非eager模式
        );
    }

    /**
     * 测试案例13: 2080Ti 22GB - 14B模型长序列
     * 场景: 中模型+长序列+块优化
     */
    public static void test2080Ti_14B_LongSeq() {
        System.out.println("\n\n===== 测试案例13: 2080Ti 22GB - 14B模型长序列 =====");
        estimateWithLog(
                14.0,   // 14B模型
                32,     // 层数
                4096,   // 隐藏层
                4,      // KV头数
                32768,  // 序列长度
                1024,   // 批次token
                1,      // TP数
                32,     // block_size(优化长序列)
                22.0,   // GPU显存(2080Ti)
                0.95,   // 利用率
                false   // 非eager模式
        );
    }

    /**
     * 测试案例14: 2080Ti 22GB - 32B模型双卡部署
     * 场景: 大模型+双卡并行
     */
    public static void test2080Ti_32B_DualGPU() {
        System.out.println("\n\n===== 测试案例14: 2080Ti 22GB - 32B模型双卡部署 =====");
        estimateWithLog(
                32.0,   // 32B模型
                64,     // 层数
                5120,   // 隐藏层
                8,      // KV头数
                16384,  // 序列长度
                4096,   // 批次token
                2,      // TP数(双卡)
                16,     // block_size
                22.0,   // GPU显存(2080Ti)
                0.95,   // 利用率
                false   // 非eager模式
        );
    }

    /**
     * 测试案例15: 2080Ti 22GB - 32B模型四卡部署
     * 场景: 大模型+四卡并行+极限利用率
     */
    public static void test2080Ti_32B_QuadGPU() {
        System.out.println("\n\n===== 测试案例15: 2080Ti 22GB - 32B模型四卡部署 =====");
        estimateWithLog(
                32.0,   // 32B模型
                64,     // 层数
                5120,   // 隐藏层
                8,      // KV头数
                8192,   // 序列长度
                8192,   // 批次token
                4,      // TP数(四卡)
                8,      // block_size(小尺寸优化)
                22.0,   // GPU显存(2080Ti)
                0.98,   // 利用率
                false   // 非eager模式
        );
    }

    /**
     * 测试案例16: 2080Ti 22GB - 混合负载测试
     * 场景: 中模型+变长序列+动态批次
     */
    public static void test2080Ti_MixedWorkload() {
        System.out.println("\n\n===== 测试案例16: 2080Ti 22GB - 混合负载测试 =====");

        // 短序列场景
        System.out.println("--- 短序列场景 (4K) ---");
        estimateWithLog(
                14.0,   // 14B模型
                32,     // 层数
                4096,   // 隐藏层
                4,      // KV头数
                4096,   // 序列长度
                8192,   // 批次token
                1,      // TP数
                8,      // block_size(小尺寸优化)
                22.0,   // GPU显存(2080Ti)
                0.95,   // 利用率
                false   // 非eager模式
        );

        // 长序列场景
        System.out.println("\n--- 长序列场景 (32K) ---");
        estimateWithLog(
                14.0,   // 14B模型
                32,     // 层数
                4096,   // 隐藏层
                4,      // KV头数
                32768,  // 序列长度
                1024,   // 批次token
                1,      // TP数
                32,     // block_size(大尺寸优化)
                22.0,   // GPU显存(2080Ti)
                0.95,   // 利用率
                false   // 非eager模式
        );
    }

    public static void main(String[] args) {
//        // 基础场景测试
//        testQwen7B_P100();
//        testQwen14B_RTX4090();
//        testQwen32B_A100();
//
//        // 高级场景测试
//        testExtremeScenario();
//        testMultiGPUDeployment();
//        testSmallBatchOptimization();
//        testUtilizationImpact();

        //针对P100和2080ti的测试
        // P100 16GB显卡测试
//        testP100_7B_ShortSeq();
//        testP100_7B_LongSeq();
//        testP100_14B_Extreme();

        // 2080Ti 22GB显卡测试
//        test2080Ti_7B_HighThroughput();
//        test2080Ti_14B_Standard();
//        test2080Ti_14B_LongSeq();
//        test2080Ti_32B_DualGPU();
//        test2080Ti_32B_QuadGPU();
//        test2080Ti_MixedWorkload();
    }
}