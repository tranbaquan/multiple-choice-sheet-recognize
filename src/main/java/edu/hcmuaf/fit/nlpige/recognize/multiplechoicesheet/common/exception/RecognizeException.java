package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception;

public class RecognizeException extends NLPigeException {
    public RecognizeException() {
        super();
    }

    public RecognizeException(String message) {
        super(message);
    }

    public RecognizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecognizeException(Throwable cause) {
        super(cause);
    }

    public RecognizeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
