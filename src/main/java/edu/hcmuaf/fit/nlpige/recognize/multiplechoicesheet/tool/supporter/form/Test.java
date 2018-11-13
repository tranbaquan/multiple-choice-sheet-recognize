package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.supporter.form;

import com.itextpdf.text.DocumentException;

import java.io.*;

public class Test {
    public static final String HTML_FILE = "src/main/resources/output/form.html";
    public static final String DEST_FOLDER = "src/main/resources/output";
    public static final String HEADER = "src/main/resources/default/header.txt";
    public static final String BODY = "src/main/resources/default/body.txt";
    public static final String QR_CODE = "src/main/resources/images/qr_code.svg";

    public static void main(String[] args) {
        PDFGenerator pdfGenerator = new PDFGenerator();
        pdfGenerator.generateDefaultHtml(HEADER, BODY, HTML_FILE);
        pdfGenerator.setQRCode(HTML_FILE, QR_CODE);
        pdfGenerator.convertXhtmlToPdf(HTML_FILE, DEST_FOLDER);
//        pdfGenerator.convertXhtmlToPdf("src/main/resources/test1/index.html", "src/main/resources/output");
    }
}
