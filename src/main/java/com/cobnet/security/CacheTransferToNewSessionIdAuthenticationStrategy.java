package com.cobnet.security;

import com.cobnet.interfaces.spring.repository.IndexedRedisRepository;
import com.cobnet.spring.boot.configuration.SessionConfiguration;
import com.cobnet.spring.boot.controller.handler.http.HttpSessionEventHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CacheTransferToNewSessionIdAuthenticationStrategy implements SessionAuthenticationStrategy {

    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response) throws SessionAuthenticationException {

        if(authentication.getDetails() instanceof WebAuthenticationDetails details) {

            IndexedRedisRepository.updateIndex(details, request.getSession());
        }
    }
}
