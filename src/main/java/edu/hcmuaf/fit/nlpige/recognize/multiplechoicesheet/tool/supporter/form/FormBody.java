package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.supporter.form;

import java.util.ArrayList;
import java.util.List;

public class FormBody {
    private List<String> candidates;

    public FormBody() {
        this.candidates = new ArrayList<>();
    }

    public FormBody(List<String> candidates) {
        this.candidates = candidates;
    }

    public List<String> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<String> candidates) {
        this.candidates = candidates;
    }

    public void addCandidate(String candidate) {
        this.candidates.add(candidate);
    }

    public String getHtml() {
        StringBuffer buffer = new StringBuffer();
        int size = candidates.size();

        for (int i = 0; i < size - 1; i++) {
            buffer.append("<tr>\n");
            buffer.append(String.format("<td class=\"left\">%s</td>\n", candidates.get(i)));
            buffer.append("<td class=\"right\">\n");
            for (int j = 1; j < size + 1; j++) {
                buffer.append(String.format("<img class=\"bubble\" src=\"src/main/resources/icons/%s.png\" alt=\"\"/>\n", j));
                buffer.append("<span class=\"span\">&nbsp;</span>\n");
            }
            buffer.append("</td>\n</tr>\n");
        }

        buffer.append("<tr class=\"last-child\">\n");
        buffer.append(String.format("<td class=\"left\">%s</td>\n", candidates.get(size-1)));
        buffer.append("<td class=\"right\">\n");
        for (int j = 1; j < size + 1; j++) {
            buffer.append(String.format("<img class=\"bubble\" src=\"src/main/resources/icons/%s.png\" alt=\"\"/>\n", j));
            buffer.append("<span class=\"span\">&nbsp;</span>\n");
        }
        buffer.append("</td>\n</tr>\n");
        return buffer.toString();
    }
}
