package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.supporter.form;

public class Test {
    public static final String OUTPUT_FOLDER = "src/main/resources/output";
    public static final String FORM = "src/main/resources/output/form.html";
    public static final String HTML_OUTPUT = "src/main/resources/output/html";
    public static final String PDF_OUTPUT = "src/main/resources/output/pdf";
    public static final String QR_CODE = "src/main/resources/images/logo.jpg";

    public static void main(String[] args) {
        String[] candidates = {"Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam",
                "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam",
                "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam"};
        PDFGenerator pdfGenerator = new PDFGenerator();
        pdfGenerator.generateDefaultHtml(2018, "2018-2023", "HỘI ĐỒNG QUẢN TRỊ", candidates, OUTPUT_FOLDER);
        pdfGenerator.setQRCode(FORM, HTML_OUTPUT, QR_CODE);
        pdfGenerator.convertXhtmlToPdf(HTML_OUTPUT, PDF_OUTPUT);
    }
}