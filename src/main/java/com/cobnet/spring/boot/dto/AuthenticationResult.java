package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;

public record AuthenticationResult(boolean authenticated, ConnectionToken connectionToken) implements ApplicationJson {

    @Override
    public boolean authenticated() {
        return authenticated;
    }

    @Override
    public ConnectionToken connectionToken() {
        return connectionToken;
    }
}
