package com.storechain.interfaces.security.permission;

import java.util.Collection;

import com.storechain.interfaces.security.Operator;

public interface Permissible extends Operator {

	 public String getIdentity();
	
	 public Collection<? extends Permission> getPermissions();
	 
	 public default boolean isPermitted(Permission permission) {
		 
		 return this.isPermitted(permission.getAuthority());
	 }
	 
	 public boolean isPermitted(String authority);
	 
	 public <T extends Permission> void addPermission(T permission);
	 
	 public <T extends Permission> void removePermission(T permission);
}
