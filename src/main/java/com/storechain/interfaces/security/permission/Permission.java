package com.storechain.interfaces.security.permission;

import org.springframework.security.core.GrantedAuthority;

public interface Permission extends GrantedAuthority {

	public String getDescription();
	
	public int getPower();
}
