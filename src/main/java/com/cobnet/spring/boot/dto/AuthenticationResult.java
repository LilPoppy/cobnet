package com.cobnet.spring.boot.dto;

import com.cobnet.spring.boot.dto.support.HttpMapTransmission;

public class AuthenticationResult extends HttpMapTransmission {

    private final boolean authenticated;

    private final ConnectionToken token;

    public AuthenticationResult(boolean authenticated, ConnectionToken token) {

        this.authenticated = authenticated;
        this.token = token;
    }

    public boolean isAuthenticated() {

        return authenticated;
    }

    public ConnectionToken getToken() {

        return this.token;
    }
}
