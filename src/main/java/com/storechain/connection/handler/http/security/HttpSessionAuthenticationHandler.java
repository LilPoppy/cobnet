package com.storechain.connection.handler.http.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.stereotype.Component;

@Component
public class HttpSessionAuthenticationHandler implements SessionAuthenticationStrategy {

	@Override
	public void onAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response) throws SessionAuthenticationException {
		
		System.out.println("HttpSessionAuthenticationHandler");
		
		return;
	}

}
