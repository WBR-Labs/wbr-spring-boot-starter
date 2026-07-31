package com.wbr.error.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends WbrException {

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, "FORBIDDEN", message);
    }

    public ForbiddenException(String code, String message) {
        super(HttpStatus.FORBIDDEN, code, message);
    }
}
