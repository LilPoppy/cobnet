package com.cobnet.interfaces.security;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

public interface Permission extends GrantedAuthority, Serializable {

	public String getDescription();
	
	public int getPower();
}
