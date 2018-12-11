package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.supporter.form;

public class FormBody {
    private String[] candidates;

    public FormBody(String[] candidates) {
        this.candidates = candidates;
    }

    public String getHtml() {

        if (candidates == null || candidates.length == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        builder.append("<div class=\"content\">\n");
        builder.append("<div class=\"table\">\n");
        builder.append("<table>\n");
        builder.append("<tr>\n");
        builder.append("<td class=\"left\">Tên Ứng Cử Viên</td>\n");
        builder.append("<td class=\"right\" style=\"text-align: center\">Hệ Số Biểu Quyết</td>\n");
        builder.append("</tr>\n");

        int size = candidates.length;
        for (int i = 0; i < size - 1; i++) {
            builder.append("<tr>\n");
            builder.append(String.format("<td class=\"left\">%s</td>\n", (i + 1) + ". " + candidates[i]));
            builder.append("<td class=\"right\">\n");
            for (int j = 1; j < size + 1; j++) {
                builder.append(String.format("<img class=\"bubble\" src=\"src/main/resources/icons/%s.png\" alt=\"\"/>\n", j));
                builder.append("<span class=\"span\">&nbsp;</span>\n");
            }
            builder.append("</td>\n</tr>\n");
        }

        builder.append("<tr class=\"last-child\">\n");
        builder.append(String.format("<td class=\"left\">%s</td>\n", size + ". " + candidates[size - 1]));
        builder.append("<td class=\"right\">\n");
        for (int j = 1; j < size + 1; j++) {
            builder.append(String.format("<img class=\"bubble\" src=\"src/main/resources/icons/%s.png\" alt=\"\"/>\n", j));
            builder.append("<span class=\"span\">&nbsp;</span>\n");
        }
        builder.append("</td>\n</tr>\n");
        builder.append(" </table>\n</div>\n");
        builder.append("<div class=\"text-bold text-small\">Ghi chú:</div>\n");
        builder.append("<div class=\"text-small\">\n");
        builder.append(String.format("- Nếu đồng ý thì tô vào hệ số biểu quyết, tổng hệ số biểu không được vượt quá %s\n", candidates.length));
        builder.append("</div>\n");
        builder.append("<div class=\"text-small\">- Nếu không ý bầu thì không tô</div>\n");
        builder.append("</div>\n</div>\n</body>\n</html>");
        return builder.toString();
    }
}
