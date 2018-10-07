package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.types;

public enum PaperType {
    A4(596),
    A5(298);

    private final int width;

    PaperType(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }
}
