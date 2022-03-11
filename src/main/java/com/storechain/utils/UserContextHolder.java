package com.storechain.utils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;

import com.storechain.interfaces.security.Operator;
import com.storechain.security.UserAuthenticationToken;
import com.storechain.spring.boot.configuration.SecurityConfiguration;
import com.storechain.spring.boot.entity.ExternalUser;
import com.storechain.spring.boot.entity.User;

public class UserContextHolder {

	public static Operator getOperator() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication == null) {
			
			return null;
		}
		
		return (Operator) authentication.getPrincipal();
	}
	
	public static void logout() {
		
		ServletRequest request = SpringContext.getCurrentRequest();
		
		if(request instanceof HttpServletRequest) {
			
			HttpSession session = ((HttpServletRequest)request).getSession();
			session.removeAttribute(SecurityConfiguration.SESSION_KEY);
			session.invalidate();
			
			SessionInformation information = SessionManager.getSessionRegistry().getSessionInformation(session.getId());
			
			if(information != null) {
				
				information.expireNow();
			}
		}
		
		SecurityContextHolder.clearContext();;
	}
	
	public static boolean bind(User user) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication == null) {
			
			return false;
		}
		
		Operator operator = (Operator) authentication.getPrincipal();
		
		if(operator == null) {
			
			return false;
		}
		
		if((operator instanceof ExternalUser)) {
			
			user.getOwnedExternalUserCollection().add((ExternalUser)operator);
			DatabaseManager.getUserRepository().save(user);
			
			SecurityContextHolder.getContext().setAuthentication(new UserAuthenticationToken(user));
			
			if(DatabaseManager.getUserRepository() != null && !DatabaseManager.getUserRepository().existsById(user.getUsername())) {
				
				DatabaseManager.getUserRepository().save(user);
			}
			
			return true;
		}
		
		return false;
	}
	
	public static boolean bind(String username) {
		
		return UserContextHolder.bind(DatabaseManager.getUserRepository().findByUsernameIgnoreCase(username));
	}
}
