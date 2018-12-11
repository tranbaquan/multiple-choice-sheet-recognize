package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet;

import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.types.PaperType;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.core.SheetRecognize;

import java.util.List;

public class RecognizeRunner {

    public static void main(String[] args) {
//        long current = System.currentTimeMillis();
//
//        for (int i = 0; i < 500; i++) {
            String file = "src/main/resources/images/form-3.png";
            SheetRecognize sheetRecognize = new SheetRecognize();
            sheetRecognize.readFile(file);
            sheetRecognize.setQuestionNum(15);
            sheetRecognize.setPaperType(PaperType.A4);
            List<List<Integer>> answers = (List<List<Integer>>) sheetRecognize.recognize();

            for (int i = 0; i < answers.size(); i++) {
                System.out.println("Record "+ (i+1)+ ": " + answers.get(i));
            }

//        }

//        long end = System.currentTimeMillis();
//
//        System.out.println(end-current);

    }
}
