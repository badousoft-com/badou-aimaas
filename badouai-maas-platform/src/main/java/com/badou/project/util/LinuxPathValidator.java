package com.badou.project.util;

/**
 * 判断一个字符串 是否满足linux路径要求 如果满足 则返回null 代表通过 如果返回
 */
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class LinuxPathValidator {

    // 匹配非法字符（除路径分隔符外的控制字符）
    private static final Pattern INVALID_CHARS_PATTERN = Pattern.compile("[\\x00\\n\\t]");

    // 匹配是否包含非法文件名（保留名称：. 和 ..）
    private static final Pattern INVALID_FILE_NAME_PATTERN = Pattern.compile("(^|/)(\\.|\\.\\.)(/|$)");

    /**
     * 验证 Linux 路径，返回 null 表示有效，否则返回错误信息
     */
    public static String validate(String path) {
        if (path == null) {
            return "不能为 null";
        }

        if (path.isEmpty()) {
            return "不能为空";
        }

        // 1. 验证根路径（必须以 / 开头）
        if (!path.startsWith("/")) {
            return "必须以 '/' 开头";
        }

        // 2. 验证路径长度（完整路径不超过 4096 字节）
        if (path.getBytes(StandardCharsets.UTF_8).length > 4096) {
            return "长度超过4096字节";
        }

        // 3. 验证是否包含非法字符
        if (INVALID_CHARS_PATTERN.matcher(path).find()) {
            return "包含非法字符（如空字符、换行符、制表符）";
        }

        // 4. 分割路径，验证每个组件
        String[] components = path.split("/");
        for (String component : components) {
            // 跳过空组件（连续的斜杠会产生空组件）
            if (component.isEmpty()) {
                continue;
            }

            // 验证单个组件长度（不超过 255 字节）
            if (component.getBytes(StandardCharsets.UTF_8).length > 255) {
                return "组件长度超过255字节：" + component;
            }
        }

        // 5. 验证是否包含非法文件名（如 /. 或 /.. 或 /dir/..）
        if (INVALID_FILE_NAME_PATTERN.matcher(path).find()) {
            return "路径包含非法组件（如 '.' 或 '..'）";
        }

        // 所有检查通过，路径有效
        return null;
    }

    // 测试示例
    public static void main(String[] args) {
        System.out.println(validate("/home/user"));         // null
        System.out.println(validate("/home/user/"));        // null
        System.out.println(validate("/home/user/file.txt")); // null
        System.out.println(validate("/"));                  // null

        System.out.println(validate("home/user"));          // 路径必须以 '/' 开头
        System.out.println(validate("/."));                 // 路径包含非法组件（如 '.' 或 '..'）
        System.out.println(validate("/.."));                // 路径包含非法组件（如 '.' 或 '..'）
        System.out.println(validate("/home/../user"));      // 路径包含非法组件（如 '.' 或 '..'）
        System.out.println(validate("/home/user\0file"));   // 路径包含非法字符（如空字符、换行符、制表符）
        System.out.println(validate("/home/user\nfile"));   // 路径包含非法字符（如空字符、换行符、制表符）

        // 超长路径测试
        StringBuilder longPath = new StringBuilder("/");
        for (int i = 0; i < 4095; i++) {
            longPath.append("a");
        }
        System.out.println(validate(longPath.toString()));  // null
        System.out.println(validate(longPath.append("a").toString()));  // 路径长度超过 4096 字节
    }
}