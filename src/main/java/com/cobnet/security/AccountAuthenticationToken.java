package com.cobnet.security;

import com.cobnet.interfaces.security.Account;
import com.cobnet.spring.boot.entity.ExternalUser;
import com.cobnet.spring.boot.entity.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class AccountAuthenticationToken extends AbstractAuthenticationToken {

    private final Account principal;

    private Object credentials;

    public AccountAuthenticationToken(Account principal, Object credentials, Object detail) {

        super(principal.getAuthorities());

        this.principal = principal;
        this.credentials = credentials;
        this.setDetails(detail);
        this.setAuthenticated(true);
    }

    public AccountAuthenticationToken(Account principal, Object credentials) {

        this(principal, credentials, null);
    }

    public AccountAuthenticationToken(Account principal) {

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

    public Account getAccount() {

        if(this.getPrincipal() instanceof ExternalUser account) {

            return account.getUser() != null ? account.getUser() : account;
        }

        return getPrincipal();
    }

    public boolean isRegistered() {

        return this.principal instanceof User;
    }
}
