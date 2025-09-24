package com.groceryshop.shared.exception;

/**
 * Exception thrown when JWT token is missing from request
 */
public class MissingTokenException extends AuthenticationException {

    public MissingTokenException(String message) {
        super(message);
    }

    public MissingTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
