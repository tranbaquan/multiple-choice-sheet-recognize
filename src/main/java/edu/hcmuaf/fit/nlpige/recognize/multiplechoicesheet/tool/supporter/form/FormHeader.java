package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.supporter.form;

public class FormHeader {
    private int year;
    private String term;
    private String board;

    public FormHeader(int year, String term, String board) {
        this.year = year;
        this.term = term;
        this.board = board;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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
        builder.append("</div>\n");
        builder.append("</div>\n");
        builder.append("<div class=\"title\">\n");
        builder.append(String.format("<h3 class=\"normal-title\">ĐẠI HỘI CỔ ĐÔNG THƯỜNG NIÊN NĂM %s</h3>\n", year));
        builder.append("<h3 class=\"normal-title\">NGÂN HÀNG TMCP PHÁT TRIỂN</h3>\n");
        builder.append("<h3 class=\"normal-title\">THÀNH PHỐ HỒ CHÍ MINH</h3>\n");
        builder.append("<h1 class=\"nameSheet\">PHIẾU BẦU CỬ</h1>\n");
        builder.append(String.format("<h3 class=\"small-title\">THÀNH VIÊN %s</h3>\n", board));
        builder.append(String.format("<h3 class=\"small-title\">NHIỆM KỲ %s</h3>\n", term));
        builder.append("</div>\n");
        builder.append("</div>\n");
        return builder.toString();
    }
}
