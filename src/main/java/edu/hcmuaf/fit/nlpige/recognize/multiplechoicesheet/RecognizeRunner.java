package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet;

import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.core.Report;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.core.SheetRecognize;

import java.util.Arrays;


public class RecognizeRunner {

    public static void main(String[] args) {
        String file = "C:\\Users\\tranb\\Downloads\\Microsoft.SkypeApp_kzf8qxf38zg5c!App" +
                "\\All\\PBC-BKS-img\\Scan2019-04-01_114222_122.jpg";
        SheetRecognize sheetRecognize = new SheetRecognize();
        sheetRecognize.readFile(file);
        sheetRecognize.imageProc();
        sheetRecognize.setQuestionNum(3);
        int[][] answer = sheetRecognize.recognize();
        Report report = new Report(answer);
        System.out.println(report.getReport());

        System.out.println(Arrays.deepToString(report.getAnswer()));

    }
}
