package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.supporter.form;


public class PDFGenerator implements PDFGenerable{

    @Override
    public void generateDefaultHtml(int year, String term, String board, String[] body, String desFolder) {
        FormHeader formHeader = new FormHeader(year, term, board);
        FormBody formBody = new FormBody(body);
        FormUtils.generateHtml(formHeader, formBody, desFolder);
    }

    @Override
    public void setQRCode(String srcFile, String desFolder, String qrFile) {
        FormUtils.setQRCode(srcFile,desFolder, qrFile);
    }

    @Override
    public void setMultiQRCode(String srcFile, String desFolder, String qrFolder){
        FormUtils.setMultiQRCode(srcFile,desFolder, qrFolder);
    }

    @Override
    public void convertXhtmlToPdf(String srcFolder, String desFolder) {
        FormUtils.convertXhtmlToPdf(srcFolder, desFolder);
    }

    @Override
    public void convertXhtmlToOnePdf(String srcFolder, String desFolder) {
        FormUtils.convertXhtmlToOnePdf(srcFolder, desFolder);
    }

    public void exportPdf(int year, String term, String board, String[] body, String qrFolder, String outputFolder) {
        generateDefaultHtml(year, term, board, body, outputFolder);
        String form = outputFolder + "/form.html";
        String html = outputFolder + "/html";
        String pdf = outputFolder + "/pdf";
        setMultiQRCode(form, html, qrFolder);
        convertXhtmlToOnePdf(html, pdf);
    }
}
