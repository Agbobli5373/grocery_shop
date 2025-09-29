package com.groceryshop.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to authenticate with an inactive account
 */
public class AccountInactiveException extends AuthenticationException {

    public AccountInactiveException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public AccountInactiveException(String message, Throwable cause) {
        super(message, cause, HttpStatus.FORBIDDEN);
    }
}
