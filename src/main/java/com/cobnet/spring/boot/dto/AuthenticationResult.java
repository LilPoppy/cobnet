package com.cobnet.spring.boot.dto;

import com.cobnet.spring.boot.dto.support.ApplicationJsonTransmission;

public class AuthenticationResult extends ApplicationJsonTransmission {

    private final boolean authenticated;

    private final ConnectionToken connectionToken;

    private final RememberMeInfo rememberMeInfo;

    public AuthenticationResult(boolean authenticated, ConnectionToken connectionToken, RememberMeInfo rememberMeInfo) {

        this.authenticated = authenticated;
        this.connectionToken = connectionToken;
        this.rememberMeInfo = rememberMeInfo;
    }

    public boolean isAuthenticated() {

        return authenticated;
    }

    public ConnectionToken getConnectionToken() {

        return this.connectionToken;
    }

    public RememberMeInfo getRememberMeInfo() {

        return this.rememberMeInfo;
    }
}
