package com.groceryshop.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception for authentication-related errors
 */
public class AuthenticationException extends RuntimeException {

    private final HttpStatus httpStatus;

    public AuthenticationException(String message) {
        super(message);
        this.httpStatus = HttpStatus.UNAUTHORIZED;
    }

    public AuthenticationException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = HttpStatus.UNAUTHORIZED;
    }

    public AuthenticationException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
