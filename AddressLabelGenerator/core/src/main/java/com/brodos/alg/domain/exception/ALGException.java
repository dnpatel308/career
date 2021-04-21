package com.brodos.alg.domain.exception;

public class ALGException  extends RuntimeException {

    private static final long serialVersionUID = -5344707580579041954L;
    private final int code;

    public ALGException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ALGException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public ALGException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
