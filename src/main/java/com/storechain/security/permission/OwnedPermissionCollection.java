package com.storechain.security.permission;

import java.util.ArrayList;
import java.util.List;

import com.storechain.common.AbstractList;
import com.storechain.interfaces.security.permission.Permission;
import com.storechain.spring.boot.entity.UserPermission;

public class OwnedPermissionCollection extends AbstractList<Permission> {

	private List<? extends Permission> permissions;
	
	public OwnedPermissionCollection(List<UserPermission> permissions) {
		
		this.permissions = permissions;
	}
	
	public OwnedPermissionCollection() {
		
		this(new ArrayList<>());
	}
	
	
	public void add(UserPermission... permissions) {
		
		for(int i = 0; i < permissions.length; i++) {
			
			this.add(permissions[i]);
		}
	}

	@Override
	protected List<Permission> getList() {
		
		return (List<Permission>) this.permissions;
	}

	


}
