package com.badou.project.maas.tuningmodeln.model;

import com.badou.tools.memcached.Logger;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ParseWordUtil {

    private static class WordContent {
        private String level;
        private String content;
        private List<WordContent> child;

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<WordContent> getChild() {
            return child;
        }

        public void setChild(List<WordContent> child) {
            this.child = child;
        }
    }

    private static final Logger LOGGER = Logger.getLogger(String.valueOf(ParseWordUtil.class));
    // word整体样式
    private static CTStyles wordStyles = null;

    /**
     * 根据文件路径 获取本地docx文件解析成数据
     * @param filePath 本地文件路径
     * @return 解析后的数据
     * @throws IOException
     */
    public static List<WordContent> printWordDocx(String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        XWPFDocument doc = new XWPFDocument(is);
        List<XWPFParagraph> paragraphs2 = doc.getParagraphs();

        List<WordContent> wordContents = new LinkedList<>();
        int currentObjectPoint = -1;
        for (XWPFParagraph xwpfParagraph : paragraphs2) {
            String style = xwpfParagraph.getStyle();
            String text = xwpfParagraph.getParagraphText();
            System.out.println("等级:"+style+","+text);
            //标题
            if ("1".equals(style)){
                currentObjectPoint++;
                WordContent wordContent  = new WordContent();
                wordContent.setContent(text);
                wordContent.setLevel(style);
                wordContent.setChild(new LinkedList<>());
                wordContents.add(wordContent);
            }else {
                WordContent wordContent = wordContents.get(currentObjectPoint);
                WordContent child = new WordContent();
                child.setLevel(style);
                child.setContent(text);
                wordContent.getChild().add(child);
            }
        }
        return wordContents;
    }

}