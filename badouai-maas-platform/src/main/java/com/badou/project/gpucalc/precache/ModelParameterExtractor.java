package com.badou.project.gpucalc.precache;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模型参数大小提取工具类
 * 用于从模型名称中提取参数大小（支持0.5B、1.5B、8B、32B、235B等格式）
 */
public class ModelParameterExtractor {

    // 正则表达式模式：匹配数字(可以带小数点)+B的组合，如0.5B、1.5B、8B、32B、235B
    // 确保匹配的是独立的参数部分，前后为连字符或字符串边界
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("(?<=-)\\d+(\\.\\d+)?B(?=-|$)");

    /**
     * 从模型名称中提取参数大小
     *
     * @param modelName 模型名称，如"Qwen2.5-1.5B-Instruct"
     * @return 提取到的参数大小（如"1.5B"），如果未找到则返回null
     */
    public static String extractParameterSize(String modelName) {
        if (modelName == null || modelName.trim().isEmpty()) {
            return null;
        }

        Matcher matcher = PARAMETER_PATTERN.matcher(modelName);
        if (matcher.find()) {
            return matcher.group();
        }

        // 处理特殊情况，如参数在名称开头或其他位置
        Pattern fallbackPattern = Pattern.compile("\\d+(\\.\\d+)?B");
        Matcher fallbackMatcher = fallbackPattern.matcher(modelName);
        if (fallbackMatcher.find()) {
            return fallbackMatcher.group();
        }

        return null;
    }

    /**
     * 从参数大小字符串中提取数值部分
     *
     * @param parameterSize 参数大小字符串，如"1.5B"
     * @return 数值部分，如1.5；如果无法提取则返回-1
     */
    public static double extractParameterNumber(String parameterSize) {
        if (parameterSize == null || parameterSize.isEmpty()) {
            return -1;
        }

        try {
            String numberStr = parameterSize.replace("B", "");
            return Double.parseDouble(numberStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        // 测试用的模型名称列表
        String[] modelNames = {
                "DeepSeek-R1-Distill-Qwen-32B",
                "Qwen2.5-1.5B-Instruct",  // 带小数点的参数
                "Qwen2.5-72B-Instruct",
                "Qwen2.5-Coder-32B-Instruct",
                "Qwen2.5-VL-7B-Instruct",
                "Qwen3-32B",
                "QwQ-32B",
                "Qwen2.5-0.5B-Instruct",  // 带小数点的参数
                "Qwen2.5-32B-Instruct",
                "Qwen2.5-7B",
                "Qwen2.5-7B-Instruct",
                "Qwen2.5-Omni-3B",
                "Qwen2.5-VL-32B-Instruct",
                "Qwen2-VL-72B-Instruct",
                "Qwen3-8B",
                "Qwen2.5-14B-Instruct",
                "Qwen2.5-3B-Instruct",
                "Qwen3-235B-A22B-AWQ",    // 大数值参数
                "Qwen3-Embedding-8B"
        };

        // 测试提取结果
        for (String modelName : modelNames) {
            String paramSize = extractParameterSize(modelName);
            double paramNumber = extractParameterNumber(paramSize);
            System.out.printf("模型名称: %-35s 参数大小: %-6s 数值部分: %.1f%n",
                    modelName, paramSize, paramNumber);
        }
    }
}
    