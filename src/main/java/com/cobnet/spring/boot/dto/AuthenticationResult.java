package com.cobnet.spring.boot.dto;

public class AuthenticationResult extends MappedPacket {

    private final boolean authenticated;

    public AuthenticationResult(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
