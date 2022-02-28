package com.storechain.security.permission;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.storechain.interfaces.security.permission.Permission;
import com.storechain.spring.boot.entity.UserPermission;

public class OwnedPermissionCollection extends AbstractList<Permission> {

	private List<UserPermission> permissions;
	
	public OwnedPermissionCollection(List<UserPermission> permissions) {
		
		this.permissions = permissions;
	}
	
	public OwnedPermissionCollection() {
		
		this(new ArrayList<>());
	}
	
	@Override
	public Permission get(int index) {
		
		return permissions.get(index);
	}

	@Override
	public int size() {

		return permissions.size();
	}
	
	public void add(UserPermission... permissions) {
		
		for(int i = 0; i < permissions.length; i++) {
			
			this.permissions.add(permissions[i]);
		}
	}

	


}
