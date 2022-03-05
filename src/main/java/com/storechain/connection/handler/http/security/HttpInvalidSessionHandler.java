package com.storechain.connection.handler.http.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.stereotype.Component;

@Component
public class HttpInvalidSessionHandler implements InvalidSessionStrategy {

	@Override
	public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        response.setContentType("application/json;charset=utf-8");
        
        try(PrintWriter writer = response.getWriter()) {
        	
        	writer.write(JSONObject.stringToValue("session is expired!").toString().toCharArray());
        	writer.flush();
        }
	}

}
