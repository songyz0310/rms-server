package org.geekpower.common;

import java.util.Objects;

public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int errorCode;
    private String message;

    public BaseException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public BaseException(int errorCode, Throwable exp) {
        super(exp);
        this.errorCode = errorCode;
        this.message = Objects.isNull(exp.getMessage()) ? "" : exp.getMessage();
    }

    public BaseException(int errorCode, Throwable exp, String format, Object... args) {
        super(exp);
        this.errorCode = errorCode;
        this.message = exp.getMessage() + format + args.toString();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}