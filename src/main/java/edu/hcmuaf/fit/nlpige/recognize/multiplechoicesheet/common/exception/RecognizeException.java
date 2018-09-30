package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception;

public class RecognizeException extends NLPigeException {

    public RecognizeException() {
        super(ErrorCode.RECOGNIZE_EXCEPTION);
    }

    public RecognizeException(ErrorCode message) {
        super(message);
    }

    public RecognizeException(ErrorCode message, Throwable cause) {
        super(message.name(), cause);
    }

    public RecognizeException(Throwable cause) {
        super(cause);
    }

}
