package com.groceryshop.shared.exception;

/**
 * Exception thrown when JWT token has expired
 */
public class ExpiredTokenException extends AuthenticationException {

    public ExpiredTokenException(String message) {
        super(message);
    }

    public ExpiredTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
