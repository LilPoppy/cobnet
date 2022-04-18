package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.spring.boot.dto.support.AuthenticationStatus;

public record AuthenticationResult(AuthenticationStatus status, ConnectionToken connectionToken, Object result) implements ApplicationJson {

    public AuthenticationResult(AuthenticationStatus status, ConnectionToken connectionToken) {

        this(status, connectionToken, null);
    }

    @Override
    public AuthenticationStatus status() {
        return status;
    }

    @Override
    public ConnectionToken connectionToken() {
        return connectionToken;
    }
}
