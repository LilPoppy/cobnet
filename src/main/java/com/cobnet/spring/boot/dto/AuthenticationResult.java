package com.cobnet.spring.boot.dto;

import com.cobnet.spring.boot.dto.support.HttpMapTransmission;
import com.cobnet.spring.boot.entity.PersistentLogins;

public class AuthenticationResult extends HttpMapTransmission {

    private final boolean authenticated;

    private final ConnectionToken connectionToken;

    private final RememberMeInfo rememberMeInfo;

    public AuthenticationResult(boolean authenticated, ConnectionToken connectionToken, RememberMeInfo rememberMeInfo) {

        this.authenticated = authenticated;
        this.connectionToken = connectionToken;
        this.rememberMeInfo = rememberMeInfo;
    }

    public AuthenticationResult(boolean authenticated, ConnectionToken connectionToken, PersistentLogins rememberMeInfo) {

        this(authenticated, connectionToken, new RememberMeInfo(rememberMeInfo));
    }

    public AuthenticationResult() {

        this(false, null, (PersistentLogins)null);
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
