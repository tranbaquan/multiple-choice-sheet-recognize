package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.supporter.form;

public class Test {
    public static final String OUTPUT_FOLDER = "src/main/resources/output";
    public static final String FORM = "src/main/resources/output/form.html";
    public static final String HTML_OUTPUT = "src/main/resources/output/html";
    public static final String PDF_OUTPUT = "src/main/resources/output/pdf";
    public static final String HEADER = "src/main/resources/default/header.txt";
    public static final String BODY = "src/main/resources/default/body.txt";
    public static final String QR_CODE = "src/main/resources/images/qr_code.svg";

    public static void main(String[] args) {
        PDFGenerator pdfGenerator = new PDFGenerator();
        pdfGenerator.generateDefaultHtml(HEADER, BODY, OUTPUT_FOLDER);
        pdfGenerator.setQRCode(FORM, HTML_OUTPUT, QR_CODE);
        pdfGenerator.convertXhtmlToPdf(HTML_OUTPUT, PDF_OUTPUT);
    }
}
