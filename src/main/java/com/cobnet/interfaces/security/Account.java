package com.cobnet.interfaces.security;

import com.cobnet.security.AccountAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public interface Account {

    public String getUsername();

    public Collection<? extends GrantedAuthority> getAuthorities();

    public static Account getAccount() {

        if(SecurityContextHolder.getContext().getAuthentication() instanceof AccountAuthenticationToken token) {

            return token.getAccount();
        }

        return null;
    }
}
