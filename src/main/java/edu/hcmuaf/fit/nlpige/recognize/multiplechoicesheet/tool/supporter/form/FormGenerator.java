package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.supporter.form;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FormGenerator {
    Document document;
    PageSize pageSize;

    public void init() {
        try {
            File file = new File("src/main/resources/pdf/test.pdf");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            } else {
                file.delete();
            }

            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDocument = new PdfDocument(writer);
            pageSize = PageSize.A4;
            document = new Document(pdfDocument, pageSize);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addHeader() {
        try {
//        float offset = 10;
//        float width = pageSize.getWidth() - 2*offset;
//        float height = 130;
//
//        Rectangle rectangle = new Rectangle(offset, offset, width, height);

            Paragraph p1 = new Paragraph("Đại Hội Cổ Đông Thường Niên 2018")
                    .setFont(PdfFontFactory.createFont("src/main/resources/vnarial.ttf", "UTF-8"))
                    .setFontSize(17)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(p1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        document.close();
    }
}
