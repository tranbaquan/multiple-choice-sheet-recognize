package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report {
    private List<List<Integer>> data;
    private Map<Integer, Integer> answer;
    private String correctReport;
    private String failReport;
    private boolean isCorrect;

    public Report(List<List<Integer>> data) {
        this.data = data;
        isCorrect = true;
        answer = new HashMap<>();
        analyzeAnswer();
    }

    public void analyzeAnswer() {
        correctReport = "----------------REPORT----------------\n";
        failReport = "----------------REPORT----------------\n";
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).size() == 1) {
                answer.put(i + 1, data.get(i).get(0));
            } else if (data.get(i).size() == data.size()) {
                answer.put(i + 1, 0);
            } else {
                isCorrect = false;
                failReport += "- Error detected at answer " + (i + 1) + ": ";
                failReport += "Answer must be only one, Answer detected: " + data.get(i).toString();
                failReport += "\n";
            }
        }
        int sum = 0;
        for (Map.Entry<Integer, Integer> set : answer.entrySet()) {
            sum += set.getValue();
        }
        if (sum > data.size()) {
            isCorrect = false;
            failReport += "- Error detected: Total point (" + sum + ") are greater than " + data.size();
            failReport += "\n";
        } else {
            correctReport += "- Answer:\n";
            for (Map.Entry<Integer, Integer> set : answer.entrySet()) {
                correctReport += "\tReport " + set.getKey() + ": " + set.getValue() + "\n";
            }
            correctReport += "Total point: " + sum;
        }
    }

    public String getReport() {
        return isCorrect ? correctReport : failReport;
    }

    public Map<Integer, Integer> getAnswer() {
        return answer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}
