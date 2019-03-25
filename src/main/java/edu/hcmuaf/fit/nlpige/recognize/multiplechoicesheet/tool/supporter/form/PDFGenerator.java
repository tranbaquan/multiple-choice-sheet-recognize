package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.supporter.form;


public class PDFGenerator implements PDFGenerable {
    private FormUtils formUtils;

    public PDFGenerator() {
        formUtils = new FormUtils();
    }

    @Override
    public void generateDefaultHtml(int year, String term, String board, String[] body, String desFolder) {
        FormHeader formHeader = new FormHeader(year, term, board);
        FormBody formBody = new FormBody(body);
        formUtils.generateHtml(formHeader, formBody, desFolder);
    }

    @Override
    public void setQRCode(String srcFile, String desFolder, String qrFile) {
        formUtils.setQRCode(srcFile, desFolder, qrFile);
    }

    @Override
    public void setMultiQRCode(String srcFile, String desFolder, String qrFolder) {
        formUtils.setMultiQRCode(srcFile, desFolder, qrFolder);
    }

    @Override
    public void convertXhtmlToPdf(String srcFolder, String desFolder) {
        formUtils.convertXhtmlToPdf(srcFolder, desFolder);
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
        generateDefaultHtml(year, term, board, body, outputFolder);
        String form = outputFolder + "/form.html";
        formUtils.optConvert(form, outputFolder + "/" + outputFileName, qrFolder);
    }


}
