package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.core;


import java.util.Arrays;

public class Report {
    private int[][] data;
    private int[][] answer;
    private String correctReport;
    private String failReport;
    private boolean isCorrect;

    public Report(int[][] data) {
        this.data = data;
        isCorrect = true;
        answer = new int[data.length][1];
        analyzeAnswer();
    }

    public void analyzeAnswer() {
        correctReport = "----------------REPORT----------------\n";
        failReport = "----------------REPORT----------------\n";
        for (int i = 0; i < data.length; i++) {
            if (data[i].length == 1) {
                answer[i][0] = data[i][0];
            } else if (data[i].length == data.length) {
                answer[i][0] = 0;
            } else {
                isCorrect = false;
                failReport += "- Error detected at answer " + (i + 1) + ": ";
                failReport += "Answer must be only one, Answer detected: " + Arrays.toString(data[i]);
                failReport += "\n";
            }
        }
        int sum = 0;
        for (int i = 0; i < answer.length; i++) {
            sum += answer[i][0];
        }
        if (sum > data.length) {
            isCorrect = false;
            failReport += "- Error detected: Total point (" + sum + ") are greater than " + data.length;
            failReport += "\n";
        } else {
            correctReport += "- Answer:\n";
            for (int i = 0; i< answer.length; i++) {
                correctReport += "\tReport " + (i+1) + ": " + answer[i][0] + "\n";
            }
            correctReport += "Total point: " + sum;
        }
    }

    public String getReport() {
        return isCorrect ? correctReport : failReport;
    }

    public int[][] getAnswer() {
        return answer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}
