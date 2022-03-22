package com.cobnet.event.account;

import com.cobnet.interfaces.security.Account;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;

public class AccountAuthenticationCreatingEvent extends AccountEvent {

    private final OAuth2LoginAuthenticationToken token;

    public AccountAuthenticationCreatingEvent(Account source, OAuth2LoginAuthenticationToken token) {

        super(source, token);
        this.token = token;
    }

    public OAuth2LoginAuthenticationToken getToken() {
        return token;
    }
}
