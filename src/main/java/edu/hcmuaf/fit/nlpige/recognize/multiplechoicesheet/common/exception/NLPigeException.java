package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception;

public class NLPigeException extends RuntimeException {
    public NLPigeException() {
    }

    public NLPigeException(ErrorCode errorCode) {
        super(errorCode.name());
    }

    public NLPigeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NLPigeException(Throwable cause) {
        super(cause);
    }

    public NLPigeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
