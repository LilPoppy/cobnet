package com.cobnet.security;

import com.cobnet.spring.boot.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.stream.Stream;

public class UserAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AccountService service;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        return service.authenticate(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return Stream.of(UsernamePasswordAuthenticationToken.class).anyMatch(clazz -> clazz.isAssignableFrom(authentication));
    }
}