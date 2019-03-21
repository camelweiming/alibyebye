package com.abb.bye.client.exception;

/**
 * @author cenpeng.lwm
 * @since 2019/3/21
 */
public class SequenceException extends RuntimeException {
    private static final long serialVersionUID = 1584435123711002373L;

    public SequenceException() {
    }

    public SequenceException(String message) {
        super(message);
    }

    public SequenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SequenceException(Throwable cause) {
        super(cause);
    }

    public SequenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
