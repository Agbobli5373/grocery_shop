package com.groceryshop.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when login credentials are invalid
 */
public class InvalidCredentialsException extends AuthenticationException {

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

}
