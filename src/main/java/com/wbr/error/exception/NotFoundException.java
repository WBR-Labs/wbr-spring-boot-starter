package com.wbr.error.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends WbrException {

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "NOT_FOUND", message);
    }

    public NotFoundException(String code, String message) {
        super(HttpStatus.NOT_FOUND, code, message);
    }
}
