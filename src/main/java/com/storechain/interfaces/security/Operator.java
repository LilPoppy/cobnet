package com.storechain.interfaces.security;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.storechain.security.RoleRule;
import org.springframework.security.core.context.SecurityContextHolder;

public interface Operator {
	
	public String getUsername();
	
	public Collection<? extends GrantedAuthority> getAuthorities();

	public static Operator getOperator() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if(authentication == null) {

			return null;
		}

		return (Operator) authentication.getPrincipal();
	}
}
