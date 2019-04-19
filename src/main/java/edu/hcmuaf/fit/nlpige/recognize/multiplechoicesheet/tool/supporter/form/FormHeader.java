package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.supporter.form;

public class FormHeader {
    private String line1;
    private String line2;
    private String line3;
    private String term;
    private String board;

    public FormHeader(String line1, String line2, String line3, String term, String board) {
        this.line1 = line1;
        this.line2 = line2;
        this.line3 = line3;
        this.term = term;
        this.board = board;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getLine3() {
        return line3;
    }

    public void setLine3(String line3) {
        this.line3 = line3;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getHtml() {
        StringBuilder builder = new StringBuilder();
        builder.append("<body>\n");
        builder.append("<div class=\"wrapper\">\n");
        builder.append("<div class=\"header\">\n");
        builder.append("<div>\n");
        builder.append("<div class=\"logo\">\n");
        builder.append("<img src=\"src/main/resources/images/logo.jpg\" alt=\"\"/>\n");
        builder.append("</div>\n");
        builder.append("<div class=\"qr-code\">\n");
        builder.append("<img src=\"%s\" alt=\"\"/>\n");
        builder.append("<div class=\"text-ss\" style=\"text-align: center\">%s</div>\n");
        builder.append("</div>\n");
        builder.append("</div>\n");
        builder.append("<div class=\"title\">\n");
        builder.append(String.format("<h3 class=\"normal-title\">%s</h3>\n", line1));
        builder.append(String.format("<h3 class=\"normal-title\">%s</h3>\n", line2));
        builder.append(String.format("<h3 class=\"normal-title\">%s</h3>\n", line3));
        builder.append("<h1 class=\"nameSheet\">PHIẾU BẦU CỬ</h1>\n");
        builder.append(String.format("<h3 class=\"small-title\">THÀNH VIÊN %s</h3>\n", board));
        builder.append(String.format("<h3 class=\"small-title\">NHIỆM KỲ %s</h3>\n", term));
        builder.append("</div>\n");
        builder.append("</div>\n");
        return builder.toString();
    }
}
