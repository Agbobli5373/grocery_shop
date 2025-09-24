package com.groceryshop.shared.exception;

/**
 * Exception thrown when user is not authenticated but tries to access protected resources
 */
public class UserNotAuthenticatedException extends AuthenticationException {

    public UserNotAuthenticatedException(String message) {
        super(message);
    }

    public UserNotAuthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
