package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet;

import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.core.Report;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.core.SheetRecognize;

import java.util.List;

public class RecognizeRunner {

    public static void main(String[] args) {

        String file = "src/main/resources/images/form-1.png";
        SheetRecognize sheetRecognize = new SheetRecognize();
        sheetRecognize.readFile(file);
        sheetRecognize.setQuestionNum(15);
        List<List<Integer>> answer = (List<List<Integer>>) sheetRecognize.recognize();

        Report report = new Report(answer);
        System.out.println(report.getReport());

    }
}
