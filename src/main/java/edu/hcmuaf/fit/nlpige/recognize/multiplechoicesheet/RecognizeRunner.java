package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet;

import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.core.SheetRecognize;
import org.opencv.core.*;

public class RecognizeRunner {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        String file = "src/main/resources/SheetRecognize.jpg";
        SheetRecognize sheetRecognize = new SheetRecognize();
        sheetRecognize.readFile(file);
        sheetRecognize.setQuestionNum(10);
        Mat processed = sheetRecognize.imageProc();
        Rect roi = sheetRecognize.detectBoundingBox(processed);
        sheetRecognize.detectRows(roi);
    }
}
