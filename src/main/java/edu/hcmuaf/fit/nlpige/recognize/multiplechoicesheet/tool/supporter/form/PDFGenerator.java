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
}
