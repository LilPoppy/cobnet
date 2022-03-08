package com.storechain.interfaces.security.permission;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.storechain.interfaces.security.Operator;
import com.storechain.security.RoleRule;

public interface Permissible {

	 public String getIdentity();
	 
	 public RoleRule getRule();
	 
	 public Collection<? extends Permission> getPermissions();
	 
	 public default boolean isPermitted(Permission permission) {
		 
		 return this.isPermitted(permission.getAuthority());
	 }
	 
	 public boolean isPermitted(String authority);
	 
	 public <T extends Permission> void addPermission(T permission);
	 
	 public <T extends Permission> void removePermission(T permission);
}
