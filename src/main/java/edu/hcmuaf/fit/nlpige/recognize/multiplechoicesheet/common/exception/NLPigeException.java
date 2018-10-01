package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NLPigeException extends RuntimeException {

    private static final Log log = LogFactory.getLog(NLPigeException.class);

    public NLPigeException(ErrorCode errorCode) {
        super(errorCode.name());
        log.error(errorCode.toString());
    }

    public NLPigeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NLPigeException(Throwable cause) {
        super(cause);
    }

}
