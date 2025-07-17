package com.badou.project.kubernetes.util;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class ZipUtils {

    /**
     * 创建压缩包并返回字节数组
     * @param zipRequests 需要压缩的文件路径列表
     * @param zipFileName 压缩包输出路径（如果为null则只返回字节数组）
     * @return 包含压缩包字节数组和实际文件路径的封装对象
     */
    public static ZipResult createZipArchive(List<ZipRequest> zipRequests, String zipFileName) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ZipOutputStream zos = new ZipOutputStream(bos)) {

                // 设置压缩参数
                zos.setLevel(Deflater.BEST_COMPRESSION);
                zos.setComment("Auto-generated archive");

                // 压缩文件内容
                for (ZipRequest zipRequest : zipRequests) {
                    File file = new File(zipRequest.getFilePath());
                    if (!file.exists()) {
                        System.err.println("跳过不存在文件: " + zipRequest.getFilePath());
                        continue;
                    }

                    try (FileInputStream fis = new FileInputStream(file)) {
                        ZipEntry entry = new ZipEntry(file.getName());
                        zos.putNextEntry(entry);

                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = fis.read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                        }
                        zos.closeEntry();
                    }
                }

                zos.finish();
                byte[] zipBytes = bos.toByteArray();

                // 写入文件系统（如果指定了路径）
                String actualPath = null;
                if (zipFileName != null) {
                    actualPath = writeToFile(zipBytes, zipFileName);
                }

                return new ZipResult(zipBytes, actualPath);
            }
        }
    }

    // 删除压缩包的方法
    public static void cleanupCompressedFile(String zipFilePath) {
        if (zipFilePath != null && !zipFilePath.isEmpty()) {
            try {
                Path path = Paths.get(zipFilePath);
                Files.deleteIfExists(path);
                log.info("压缩包已删除: " + zipFilePath);
                zipFilePath = null; // 重置路径
            } catch (IOException e) {
                e.printStackTrace();
                log.error("删除压缩包时出错: " + e.getMessage());
            }
        }
    }

    /**
     * 将字节数组写入文件
     */
    private static String writeToFile(byte[] data, String path) throws IOException {
        Path outputPath = Paths.get(path);
        Files.createDirectories(outputPath.getParent()); // 自动创建目录
        Files.write(outputPath, data);
        return outputPath.toAbsolutePath().toString();
    }

    /**
     * 结果封装类
     */
    public static class ZipResult {
        private final byte[] bytes;
        private final String filePath;

        public ZipResult(byte[] bytes, String filePath) {
            this.bytes = bytes;
            this.filePath = filePath;
        }

        // Getter方法
        public byte[] getBytes() { return bytes; }
        public String getFilePath() { return filePath; }
    }

    /**
     * 结果封装类
     */
    public static class ZipRequest {
        private final String fileName;
        private final String filePath;

        public ZipRequest(String fileName, String filePath) {
            this.fileName = fileName;
            this.filePath = filePath;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFilePath() {
            return filePath;
        }
    }
}
