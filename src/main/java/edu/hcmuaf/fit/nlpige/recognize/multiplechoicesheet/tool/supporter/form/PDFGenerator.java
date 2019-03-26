package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.supporter.form;


import com.itextpdf.text.DocumentException;

import java.io.IOException;

public class PDFGenerator implements PDFGenerable {
    private FormUtils formUtils;

    public PDFGenerator() {
        formUtils = new FormUtils();
    }

    @Override
    public void generateDefaultHtml(int year, String term, String board, String[] body, String desFolder) {
        try {
            FormHeader formHeader = new FormHeader(year, term, board);
            FormBody formBody = new FormBody(body);
            formUtils.generateHtml(formHeader, formBody, desFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setQRCode(String srcFile, String desFolder, String qrFile) {
        try {
            formUtils.setQRCode(srcFile, desFolder, qrFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMultiQRCode(String srcFile, String desFolder, String qrFolder) {
        try {
            formUtils.setMultiQRCode(srcFile, desFolder, qrFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void convertXhtmlToPdf(String srcFolder, String desFolder) {
        try {
            formUtils.convertXhtmlToPdf(srcFolder, desFolder);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void convertXhtmlToOnePdf(String srcFolder, String desFolder) {
        formUtils.convertXhtmlToOnePdf(srcFolder, desFolder);
    }

//    public void exportPdf(int year, String term, String board, String[] body, String qrFolder, String outputFolder) {
//        generateDefaultHtml(year, term, board, body, outputFolder);
//        String form = outputFolder + "/form.html";
//        String html = outputFolder + "/html";
//        String pdf = outputFolder + "/pdf";
//        setMultiQRCode(form, html, qrFolder);
//        convertXhtmlToOnePdf(html, pdf);
//    }

    public void exportPdf(int year, String term, String board, String[] body, String qrFolder, String outputFolder, String outputFileName) {
        try {
            generateDefaultHtml(year, term, board, body, outputFolder);
            String form = outputFolder + "/form.html";
            formUtils.optConvert(form, outputFolder + "/" + outputFileName, qrFolder);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
    }


}
