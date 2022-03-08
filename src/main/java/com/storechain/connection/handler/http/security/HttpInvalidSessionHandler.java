package com.storechain.connection.handler.http.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.stereotype.Component;

import com.storechain.spring.boot.configuration.SecurityConfiguration;
import com.storechain.utils.SpringContext;

@Component
public class HttpInvalidSessionHandler implements InvalidSessionStrategy {

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Override
	public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        response.setContentType("application/json;charset=utf-8");
        
        SpringContext.getSecurityConfiguration().getSessionRegistry().removeSessionInformation(request.getSession().getId());
        
        request.getSession().removeAttribute(SecurityConfiguration.SESSION_KEY);
        request.getSession().invalidate();
        
        redirectStrategy.sendRedirect(request, response, request.getHeader("Referer"));
	}

}
