package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet;

import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.core.Report;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.core.SheetRecognize;

import java.util.Arrays;


public class RecognizeRunner {

    public static void main(String[] args) {

        String file = "D:\\Workspace\\OpenCV\\MultipleChoiceSheetRecognize\\src\\main\\resources\\images\\form-12.png";
        SheetRecognize sheetRecognize = new SheetRecognize();
        sheetRecognize.readFile(file);
        sheetRecognize.imageProc();
        System.out.println(sheetRecognize.getQrCode());
        sheetRecognize.setQuestionNum(15);
        int[][] answer = sheetRecognize.recognize();
        Report report = new Report(answer);
        System.out.println(report.getReport());

        System.out.println(Arrays.deepToString(report.getAnswer()));

    }
}
