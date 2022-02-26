package com.storechain.connection.handler.http.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class HttpAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

		response.setContentType("application/json;charset=utf-8");
		
        try(PrintWriter writer = response.getWriter()) {
        	
        	writer.write(JSONObject.stringToValue("access denied!").toString().toCharArray());
        	writer.flush();
        }
		
	}

}
