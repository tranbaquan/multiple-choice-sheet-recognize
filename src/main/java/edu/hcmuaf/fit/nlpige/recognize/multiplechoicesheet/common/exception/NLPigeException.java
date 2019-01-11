package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception;

public class NLPigeException extends RuntimeException {

    public NLPigeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }

    public NLPigeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NLPigeException(Throwable cause) {
        super(cause);
    }

}
