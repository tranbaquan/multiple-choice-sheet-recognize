package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet;

import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.core.SheetRecognize;
import org.opencv.core.*;

import java.util.List;

public class RecognizeRunner {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        long current = System.currentTimeMillis();

        for (int i = 0; i < 50; i++) {
            String file = "src/main/resources/images/bubbleSheetv4_1.png";
            SheetRecognize sheetRecognize = new SheetRecognize();
            sheetRecognize.readFile(file);
            sheetRecognize.setQuestionNum(10);
            sheetRecognize.imageProc();
            sheetRecognize.detectBoundingBox();
            List<Rect> records = sheetRecognize.detectRows();
            List<List<Rect>> allChoices = sheetRecognize.detectBubbles(records);
            List<List<Integer>> answers = sheetRecognize.recognizeAnswer(allChoices, records);

//            System.out.println(answers.size());
//            for (int i = 0; i < answers.size(); i++) {
//                System.out.println("Record "+ (i+1)+ ": " + answers.get(i));
//            }

        }

        long end = System.currentTimeMillis();

        System.out.println(end-current);
    }
}
