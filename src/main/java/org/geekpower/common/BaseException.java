package org.geekpower.common;

public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int errorCode;
    private String message;

    public BaseException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public BaseException(BaseError error, Throwable exp) {
        super(exp);
        this.errorCode = error.getCode();
        this.message = error.getDescription(CurrentContext.getLanguage().getLocale());
    }

    public BaseException(BaseError error, Object... args) {
        super(error.getDescription(CurrentContext.getLanguage().getLocale(), args));
        this.errorCode = error.getCode();
        this.message = super.getMessage();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}