package com.cobnet.exception;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationCancelledException extends AuthenticationException {

    public AuthenticationCancelledException(String message) {

        super(message);
    }
}
