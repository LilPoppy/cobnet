package com.storechain.connection.handler.http.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.storechain.spring.boot.configuration.SecurityConfiguration;

@Component
public class HttpOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

		System.out.println("onAuthenticationSuccess:" + authentication);

		redirectStrategy.sendRedirect(request, response, request.getSession().getAttribute(SecurityConfiguration.PREVIOUS_URL).toString());
	}
	
	

}
