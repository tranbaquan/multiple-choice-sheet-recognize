package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.supporter.form;

public class Test {
    public static final String OUTPUT_FOLDER = "src/main/resources/output";
    public static final String FORM = "src/main/resources/output/form.html";
    public static final String HTML_OUTPUT = "src/main/resources/output/html";
    public static final String PDF_OUTPUT = "src/main/resources/output/pdf";
    public static final String QR_CODE = "src/main/resources/images/qr_code.png";

    public static void main(String[] args) {
        String[] candidates = {"Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam", "Nguyễn Văn Nam"};
        PDFGenerator pdfGenerator = new PDFGenerator();
        pdfGenerator.generateDefaultHtml("ABCCCCCC", "DFEEEE", "AAAAAAAAAAAAAAA", "2018-2023", "HỘI ĐỒNG QUẢN TRỊ", candidates, OUTPUT_FOLDER);
//        pdfGenerator.setQRCode(FORM, HTML_OUTPUT, QR_CODE);
//        pdfGenerator.convertXhtmlToPdf(HTML_OUTPUT, PDF_OUTPUT);
//        pdfGenerator.exportPdf(2018, "2018-2023", "HỘI ĐỒNG QUẢN TRỊ", candidates, "src/main/resources/qr", OUTPUT_FOLDER);

        long start = System.currentTimeMillis();
//        pdfGenerator.exportPdf("DAjiasdfghjhgfdertyusdf", "2018-2023", "HỘI ĐỒNG QUẢN TRỊ", candidates, "D:/qr", "D:/output", "out.pdf");
        System.out.println(System.currentTimeMillis() - start);

    }
}
