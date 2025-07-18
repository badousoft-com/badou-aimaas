package com.badou.project.maas.tuningmodeln;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.List;

//这个类是为了读取到TextPosition的数据
public class PDFTextStripper2 extends PDFTextStripper {

    public PDFTextStripper2() throws IOException {
    }

    @Override
    public List<List<TextPosition>> getCharactersByArticle() {
        return super.getCharactersByArticle();
    }

}

