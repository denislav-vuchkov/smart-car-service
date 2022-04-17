package com.smart.garage.exceptions;

public class AuthenticationFailureException extends RuntimeException {

    public AuthenticationFailureException() {
    }

    public AuthenticationFailureException(String message) {
        super(message);
    }
}
