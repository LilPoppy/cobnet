package com.storechain.connection.handler.http.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.storechain.interfaces.security.Operator;
import com.storechain.spring.boot.configuration.SecurityConfiguration;
import com.storechain.spring.boot.entity.ExternalUser;
import com.storechain.spring.boot.entity.User;
import com.storechain.utils.DatabaseManager;
import com.storechain.utils.UserContextHolder;

@Component
public class HttpOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

		System.out.println("bind to user: " + UserContextHolder.bind("admin"));
		
		HttpSession session = request.getSession();
		
		if(session != null) {
			
			Object url = session.getAttribute(SecurityConfiguration.PREVIOUS_URL);
			
			if(url != null) {
				
				redirectStrategy.sendRedirect(request, response, url.toString());
			}
		}
	}
	
	

}
