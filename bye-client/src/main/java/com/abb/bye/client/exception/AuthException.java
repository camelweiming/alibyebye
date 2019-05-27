package com.abb.bye.client.exception;

/**
 * @author cenpeng.lwm
 * @since 2019/5/27
 */
public class AuthException extends RuntimeException {
    private static final long serialVersionUID = 1584435123711002373L;

    public AuthException() {
    }

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthException(Throwable cause) {
        super(cause);
    }

    public AuthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
