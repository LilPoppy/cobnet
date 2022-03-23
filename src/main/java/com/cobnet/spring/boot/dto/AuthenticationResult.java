package com.cobnet.spring.boot.dto;

import com.cobnet.spring.boot.dto.support.HttpMapTransmission;

public class AuthenticationResult extends HttpMapTransmission {

    private final boolean authenticated;

    public AuthenticationResult(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
