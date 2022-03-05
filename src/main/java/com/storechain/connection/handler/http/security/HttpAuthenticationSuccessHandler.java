package com.storechain.connection.handler.http.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class HttpAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

		response.setContentType("application/json;charset=utf-8");
		
        try(PrintWriter writer = response.getWriter()) {
        	
        	writer.write(JSONObject.stringToValue("login success!").toString().toCharArray());
        	writer.flush();
        }
	}

}
