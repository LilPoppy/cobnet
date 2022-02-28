package com.storechain.interfaces.security.permission;

import java.util.Collection;

import com.storechain.interfaces.security.Operator;

public interface Permissible extends Operator {

	 public String getIdentity();
	
	 public Collection<? extends Permission> getPermissions();
	 
	 public default boolean hasPermitted(Permission permission) {
		 
		 return this.hasPermitted(permission.getAuthority());
	 }
	 
	 public boolean hasPermitted(String authority);
	 
	 public <T extends Permission> void addPermission(T permission);
	 
	 public <T extends Permission> void removePermission(T permission);
}
