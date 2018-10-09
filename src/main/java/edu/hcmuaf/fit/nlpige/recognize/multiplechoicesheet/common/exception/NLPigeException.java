package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NLPigeException extends RuntimeException {

    private static final Log log = LogFactory.getLog(NLPigeException.class);

    public NLPigeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        log.error(errorCode.getMessage());
    }

    public NLPigeException(String message, Throwable cause) {
        super(message, cause);
        log.error(message);
        log.error(cause.getMessage());
    }

    public NLPigeException(Throwable cause) {
        super(cause);
        log.error(cause.getMessage());
    }

}
