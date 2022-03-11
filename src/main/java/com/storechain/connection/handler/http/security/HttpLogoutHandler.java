package com.storechain.connection.handler.http.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.storechain.utils.UserContextHolder;

@Component
public class HttpLogoutHandler implements LogoutHandler {

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

		if(authentication == null || !authentication.isAuthenticated()) {
			
			response.setContentType("application/json;charset=utf-8");
			
	        try(PrintWriter writer = response.getWriter()) {
	        	
	        	writer.write(JSONObject.stringToValue("You must be sign in first!").toString().toCharArray());
	        	writer.flush();
	        	return;
	        	
	        } catch (IOException e) {
	        	
				e.printStackTrace();
			}	
		}
		
		UserContextHolder.logout();
		
	}

}
