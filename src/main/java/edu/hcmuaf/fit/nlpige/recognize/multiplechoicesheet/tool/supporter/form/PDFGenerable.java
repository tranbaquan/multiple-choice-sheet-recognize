package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.supporter.form;

public interface PDFGenerable {

    void generateDefaultHtml(String line1, String line2, String line3, String term, String board, String[] body, String desFolder);

    void setQRCode(String srcFile, String desFolder, String qrFile);

    void setMultiQRCode(String srcFile, String desFolder, String qrFolder);

    void convertXhtmlToPdf(String srcFolder, String desFolder);

    void convertXhtmlToOnePdf(String srcFolder, String desFolder);
}
