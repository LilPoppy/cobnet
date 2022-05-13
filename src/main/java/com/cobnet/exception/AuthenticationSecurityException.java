package com.cobnet.exception;

import com.cobnet.exception.support.AuthenticationStatus;
import org.springframework.security.core.AuthenticationException;

public class AuthenticationSecurityException extends AuthenticationException {

    private final AuthenticationStatus status;

    public AuthenticationSecurityException(AuthenticationStatus status, String message) {

        super(message);
        this.status = status;
    }

    public AuthenticationSecurityException(AuthenticationStatus status) {

        super(status.name());
        this.status = status;
    }

    public AuthenticationStatus getStatus() {
        return status;
    }
}
