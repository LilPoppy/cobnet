package com.cobnet.security;

import com.cobnet.interfaces.security.Account;
import com.cobnet.spring.boot.entity.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class UserAuthenticationToken extends AbstractAuthenticationToken {

    private final Account principal;

    private Object credentials;

    public UserAuthenticationToken(Account principal, Object credentials) {

        super(principal.getAuthorities());

        this.principal = principal;
        this.credentials = credentials;
        this.setAuthenticated(true);
    }

    public UserAuthenticationToken(Account principal) {

        this(principal, "");
    }

    @Override
    public Object getCredentials() {

        return this.credentials;
    }

    @Override
    public Account getPrincipal() {

        return this.principal;
    }

    public boolean isRegistered() {

        return this.principal instanceof User;
    }
}
