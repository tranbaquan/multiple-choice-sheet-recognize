package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet;

import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.core.SheetRecognize;
import org.opencv.core.*;

import java.util.List;

public class RecognizeRunner {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        String file = "src/main/resources/bubbleSheetv3_increase-pixel.jpg";
        SheetRecognize sheetRecognize = new SheetRecognize();
        sheetRecognize.readFile(file);
        sheetRecognize.setQuestionNum(10);
        sheetRecognize.imageProc();
        sheetRecognize.detectBoundingBox();
        List<Rect> records = sheetRecognize.detectRows();
        List<List<Rect>> allChoices = sheetRecognize.detectBubbles(records);
        List<List<Integer>> answers = sheetRecognize.recognizeAnswer(allChoices, records);

        System.out.println(answers.size());
        for (int i = 0; i < answers.size(); i++) {
            System.out.println("Record "+ (i+1)+ ": " + answers.get(i));
        }
    }
}
