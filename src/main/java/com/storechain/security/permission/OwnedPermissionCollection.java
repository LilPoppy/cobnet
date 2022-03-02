package com.storechain.security.permission;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import com.storechain.common.AbstractSet;
import com.storechain.common.MultiwayTreeNode;
import com.storechain.interfaces.security.permission.Permissible;
import com.storechain.spring.boot.entity.EntityBase;
import com.storechain.spring.boot.entity.UserPermission;
import com.storechain.utils.DatabaseManager;

public class OwnedPermissionCollection extends AbstractSet<UserPermission> {
	
	private final Set<UserPermission> permissions;
	
	public OwnedPermissionCollection(Set<UserPermission> permissions) {
		
		this.permissions = permissions;
	}
	
	@Override
	protected Set<UserPermission> getSet() {
		
		return this.permissions;
	}
	
	@Override
	public boolean add(UserPermission permission) {
		
		if(permission instanceof UserPermission) {
			
			if(DatabaseManager.getUserPermissionRepository() != null) {
				
				Optional<UserPermission> optional = DatabaseManager.getUserPermissionRepository().findOne(Example.of((UserPermission)permission, ExampleMatcher.matching().withIgnorePaths("last_modify_time").withIgnoreCase()));
				
				if(optional.isEmpty()) {
					
					DatabaseManager.getUserPermissionRepository().save((UserPermission) permission);	
				} else {
					
					if(optional.get().getLastModfiedTime().before(((UserPermission) permission).getLastModfiedTime())) {
						
						DatabaseManager.getUserPermissionRepository().save((UserPermission) permission);	
					}
				}
			}
		}

		return super.add(permission);
	}
	

	public void add(UserPermission... permissions) {
		
		for(int i = 0; i < permissions.length; i++) {
			
			this.add(permissions[i]);
		}
	}
	
	public boolean hasPermission(UserPermission permission) {
		
		MultiwayTreeNode<String> root = new MultiwayTreeNode<String>("collection");
		
		for(UserPermission content : this) {
			
			root.add(MultiwayTreeNode.from(content.getAuthority()));
		}
		
		return root.contains(MultiwayTreeNode.from(permission.getAuthority()), "*");
	}
	
	public boolean hasPermission(String permission) {
		
		return this.hasPermission(new UserPermission(permission));
	}
}
