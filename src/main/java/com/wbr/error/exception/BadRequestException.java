package com.wbr.error.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends WbrException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message);
    }

    public BadRequestException(String code, String message) {
        super(HttpStatus.BAD_REQUEST, code, message);
    }
}
