package com.storechain.interfaces.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.storechain.security.RoleRule;

public interface Operator {
	
	public String getUsername();
	
	public Collection<? extends GrantedAuthority> getAuthorities();
	
}
