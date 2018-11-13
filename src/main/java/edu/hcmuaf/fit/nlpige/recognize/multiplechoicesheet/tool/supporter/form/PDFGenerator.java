package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.supporter.form;

public class PDFGenerator {

    private FormHeader formHeader;
    private FormBody formBody;

    public void generateDefaultHtml(String header, String body, String output) {
        formHeader = FormUtils.getFormHeader(header);
        formBody = FormUtils.getFormBody(body);
        FormUtils.generateHtml(formHeader, formBody, output);
    }

    public void setQRCode(String srcFile, String qrCode) {
        FormUtils.setQRCode(srcFile, qrCode);
    }

    public void convertXhtmlToPdf(String srcFile, String destFolder) {
        FormUtils.convertXhtmlToPdf(srcFile, destFolder);
    }


}
