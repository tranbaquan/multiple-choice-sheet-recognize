package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception;

public class FileNotFoundException extends NLPigeException {

    public FileNotFoundException() {
        super(ErrorCode.FILE_NOT_FOUND_EXCEPTION);
    }

    public FileNotFoundException(ErrorCode errorCode,Throwable cause) {
        super(errorCode.name(), cause);
    }
}
