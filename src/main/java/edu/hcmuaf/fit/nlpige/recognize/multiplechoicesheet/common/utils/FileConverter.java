package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.utils;

import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception.FileNotFoundException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileConverter {

    public static String toPngImage(String source) throws IOException {
        File file = new File(source);

        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        String fileType = file.getName().substring(file.getName().lastIndexOf("."));

        switch (fileType) {
            case "pdf":
                PDDocument document = PDDocument.load(file);
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
                File output = new File(file.getPath() + "abc.png");
                ImageIO.write(bim, "png", output);
                return output.getPath();
        }
        return source;
    }
}
