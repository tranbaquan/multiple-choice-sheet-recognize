package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.supporter.form;

import java.util.ArrayList;
import java.util.List;

public class FormHeader {
    private List<String> title;
    private String paperName;
    private String logo;
    private String qrCode;
    private List<String> subTitle;

    public FormHeader() {
        this.title = new ArrayList<>();
        this.subTitle = new ArrayList<>();
    }

    public List<String> getTitle() {
        return title;
    }

    public void setTitle(List<String> title) {
        this.title = title;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public List<String> getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(List<String> subTitle) {
        this.subTitle = subTitle;
    }

    public void addTitle(String title){
        this.title.add(title);
    }

    public void addSubTitle(String subTitle) {
        this.subTitle.add(subTitle);
    }

    public String getHtml() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("<img src=\"%s\" alt=\"\"/>\n", logo));
        builder.append("</div>\n");
        builder.append("<div class=\"qr-code\">\n");
        builder.append("<img src=\"%s\" alt=\"\"/>\n");
        builder.append("</div>\n</div>\n");
        builder.append("<div class=\"title\">");
        for (String t: title) {
            builder.append(String.format("<h3 class=\"normal-title\">%s</h3>", t));
            builder.append("\n");
        }
        builder.append(String.format("<h1 class=\"nameSheet\">%s</h1>", paperName));
        for (String st: subTitle) {
            builder.append(String.format("<h3 class=\"small-title\">%s</h3>", st));
            builder.append("\n");
        }
        return builder.toString();
    }
}
