package com.wbr.error.exception;

import org.springframework.http.HttpStatus;

public class UnprocessableEntityException extends WbrException {

    public UnprocessableEntityException(String message) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, "UNPROCESSABLE_ENTITY", message);
    }

    public UnprocessableEntityException(String code, String message) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, code, message);
    }
}
