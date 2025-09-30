package com.badou.project.maas.maasfile.util;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileContentExtractor {

    public static void main(String[] args) {
        String s = FileUtil.readString(new File("C:\\Users\\16532\\Desktop\\text.bat"), "UTF-8");
        List<String> strings = splitText(s, 0, 0);
        System.out.println(strings);
    }

    // 定义切割字符
    private static final String SPLIT_CHARS = ",。.\\\n";
    // 定义特殊符号，可根据需求修改
    private static final String SPECIAL_CHARS = ",。.\\!?;:\n\"'()[]{}<>/+-*=%@#$&_~";

    /**
     * 按字数/字符串 切割字符串
     * @param text
     * @return
     */
    public static List<String> splitText(String text,Integer maxLineLength,Integer limitSearch) {
        if (maxLineLength == null || maxLineLength == 0){
            maxLineLength = 1500;
        }
        if (limitSearch == null || limitSearch == 0){
            limitSearch = 500;
        }
        List<String> result = new ArrayList<>();
        int startIndex = 0;
        int length = text.length();
        while (startIndex < length) {
            // 计算大概的切割位置
            int endIndex = Math.min(startIndex + maxLineLength, length);
            log.info("开始查找切割位置，大概切割位置：" + endIndex);
            int searchEnd = Math.min(endIndex + limitSearch, length);
            boolean found = false;
            // 在 limitSearch 字符范围内查找合适的切割字符
            while (endIndex < searchEnd && !found) {
                if (SPLIT_CHARS.contains(String.valueOf(text.charAt(endIndex)))) {
                    found = true;
                    log.info("在 " + endIndex + " 位置找到合适切割字符：" + text.charAt(endIndex));
                } else {
                    endIndex++;
                }
            }
            // 如果在 limitSearch 字符内没找到合适的切割字符，找特殊符号切割
            if (!found) {
                log.info("在 limitSearch 字符内未找到合适切割字符，开始查找特殊符号。");
                while (endIndex < length && !SPECIAL_CHARS.contains(String.valueOf(text.charAt(endIndex)))) {
                    endIndex++;
                }
                if (endIndex < length) {
                    log.info("在 " + endIndex + " 位置找到特殊符号作为切割字符：" + text.charAt(endIndex));
                } else {
                    endIndex = length;
                    log.info("未找到特殊符号，切割到文本末尾。");
                }
            }
            // 提取切割的文本
            String segment = text.substring(startIndex, endIndex);
            result.add(segment);
            // 更新起始位置
            startIndex = endIndex;
        }
        return result;
    }


    /**
     * 根据文件字节数组和扩展名提取内容
     *
     * @param fileData      文件字节数组
     * @param fileExtension 文件扩展名 (e.g., "docx", "pdf", "xlsx", "xls", "doc")
     * @return 提取的文本内容
     */
    public static String extractContent(byte[] fileData, String fileExtension,String filePath) throws IOException {
        switch (fileExtension.toLowerCase()) {
            case "txt":
                return new String(fileData);
            case "bat":
                return new String(fileData);
            case "yaml":
                return new String(fileData);
            case "json":
                return new String(fileData);
            case "jsonl":
                return new String(fileData);
            case "docx":
                return extractContentFromWord(fileData);
            case "doc":
                return extractContentFromOldWord(fileData);
            case "pdf":
                return extractContentFromPDF(filePath);
            case "xlsx":
            case "xls":
                return extractContentFromExcel(fileData, fileExtension);
            default:
                throw new UnsupportedOperationException("不支持解析的文件类型: " + fileExtension);
        }
    }

    // Word 文档处理（支持表格）
    private static String extractContentFromWord(byte[] fileData) throws IOException {
        StringBuilder content = new StringBuilder();
        try (ByteArrayInputStream bis = new ByteArrayInputStream(fileData);
             XWPFDocument document = new XWPFDocument(bis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

            // 提取正文文本
            content.append(extractor.getText());

            // 提取表格内容（表格中的文字）
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        content.append(cell.getText().trim()).append("\t");
                    }
                    content.append("\n");
                }
            }

            // 注意：图片需要额外处理（示例只提取文字）
        }
        return content.toString();
    }

    // 老版本 Word 文档处理（.doc 格式）
    private static String extractContentFromOldWord(byte[] fileData) throws IOException {
//        StringBuilder content = new StringBuilder();
//        ByteArrayInputStream bis = new ByteArrayInputStream(fileData);
//        HWPFDocument document = new HWPFDocument(bis);
//        WordExtractor extractor = new WordExtractor(document);
//        // 提取正文文本
//        String[] paragraphs = extractor.getParagraphText();
//        for (String paragraph : paragraphs) {
//            content.append(paragraph.trim()).append("\n");
//        }
//
//        // 这里可以进一步扩展对表格等元素的处理
//        // 目前仅提取了段落文本
//
        return null;
    }

    // PDF文档处理
    private static String extractContentFromPDF(String filePath) throws IOException {
        File file = new File(filePath);
        RandomAccessFile is = new RandomAccessFile(file, "r");
        PDFParser parser = new PDFParser(is);
        parser.parse();
        PDDocument doc = parser.getPDDocument();
        PDFTextStripper textStripper = new PDFTextStripper();
        String s = textStripper.getText(doc);
        doc.close();
        return s;
    }

    // Excel 文档处理（支持公式和数值）
    private static String extractContentFromExcel(byte[] fileData, String fileExtension) throws IOException {
        StringBuilder content = new StringBuilder();
        try (ByteArrayInputStream bis = new ByteArrayInputStream(fileData);
             Workbook workbook = fileExtension.equalsIgnoreCase("xlsx") ?
                     new XSSFWorkbook(bis) : new HSSFWorkbook(bis)) {

            for (Sheet sheet : workbook) {
                content.append("Sheet: ").append(sheet.getSheetName()).append("\n");
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        content.append(getCellValue(cell)).append("\t");
                    }
                    content.append("\n");
                }
            }
        }
        return content.toString();
    }

    // 获取单元格值的通用方法
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
//        switch (cell.getCellType()) {
//            case STRING:
//                return cell.getStringCellValue().trim();
//            case NUMERIC:
////                return DataFormatter.getInstance().formatCellValue(cell);
//                return cell.getStringCellValue().trim();
//            case BOOLEAN:
//                return String.valueOf(cell.getBooleanCellValue());
//            case FORMULA:
//                return handleFormulaCell(cell);
//            default:
//                return "";
//        }
        return null;
    }

    // 处理公式单元格（获取计算结果）
    private static String handleFormulaCell(Cell cell) {
        try {
            return new DataFormatter().formatCellValue(cell);
        } catch (Exception e) {
            return "公式计算错误: " + cell.getCellFormula();
        }
    }

}