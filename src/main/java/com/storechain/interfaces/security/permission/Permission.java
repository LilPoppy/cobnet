package com.storechain.interfaces.security.permission;

import org.springframework.security.core.GrantedAuthority;

public interface Permission extends GrantedAuthority, Comparable<Permission> {

	public String getName();
	
	public String getDescription();
	
	public int getPower();
	
	public int compareTo(String authority);
	
	public default int compareTo(Permission permission) {
		
		return this.compareTo(permission.getAuthority());
	}
}
