package com.cobnet.exception;

import org.springframework.security.core.AuthenticationException;

public class HumanValidationFailureException extends AuthenticationException {

    public HumanValidationFailureException(String message) {

        super(message);
    }
}
