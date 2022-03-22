package com.cobnet.exception;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationCreateException extends AuthenticationException {

    public AuthenticationCreateException(String message) {

        super(message);
    }
}
