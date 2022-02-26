package com.storechain.connection.handler.http.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component("HttpAuthenticationFailureHandler")
public class HttpAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		
		response.setContentType("application/json;charset=utf-8");
		
        try(PrintWriter writer = response.getWriter()) {
        	
        	writer.write(JSONObject.stringToValue("login failure!").toString().toCharArray());
        	writer.flush();
        }
	}

}
