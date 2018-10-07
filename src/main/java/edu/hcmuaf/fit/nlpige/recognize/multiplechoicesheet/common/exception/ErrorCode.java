package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception;

public enum ErrorCode {
    FILE_NOT_FOUND_EXCEPTION("Can't find file"),
    FILE_PERMISSION_EXCEPTION("Can't read file"),
    RECOGNIZE_EXCEPTION("Can't recognize answer");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
