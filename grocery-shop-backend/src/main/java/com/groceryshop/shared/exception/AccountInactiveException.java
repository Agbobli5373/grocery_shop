package com.groceryshop.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to authenticate with an inactive account
 */
public class AccountInactiveException extends RuntimeException {

    private final HttpStatus httpStatus;

    public AccountInactiveException(String message) {
        super(message);
        this.httpStatus = HttpStatus.FORBIDDEN;
    }

    public AccountInactiveException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public AccountInactiveException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = HttpStatus.FORBIDDEN;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
