package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.supporter.form;

public class PDFGenerator {

    private FormHeader formHeader;
    private FormBody formBody;

    public void generateDefaultHtml(String header, String body, String desFolder) {
        formHeader = FormUtils.getFormHeader(header);
        formBody = FormUtils.getFormBody(body);
        FormUtils.generateHtml(formHeader, formBody, desFolder);
    }

    public void setQRCode(String srcFile, String desFolder, String qrCode) {
        FormUtils.setQRCode(srcFile,desFolder, qrCode);
    }

    public void convertXhtmlToPdf(String srcFolder, String desFolder) {
        FormUtils.convertXhtmlToPdf(srcFolder, desFolder);
    }


}
