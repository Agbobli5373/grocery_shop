package com.groceryshop.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to register a user that already exists
 */
public class UserAlreadyExistsException extends RuntimeException {

    private final HttpStatus httpStatus;

    public UserAlreadyExistsException(String message) {
        super(message);
        this.httpStatus = HttpStatus.CONFLICT;
    }

    public UserAlreadyExistsException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = HttpStatus.CONFLICT;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
