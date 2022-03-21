package com.cobnet.spring.boot.dto;

import com.cobnet.spring.boot.dto.support.MappedPacket;

public class AuthenticationResult extends MappedPacket {

    private final boolean authenticated;

    public AuthenticationResult(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
