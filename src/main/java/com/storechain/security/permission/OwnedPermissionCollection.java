package com.storechain.security.permission;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import com.storechain.common.AbstractSet;
import com.storechain.common.MultiwayTreeNode;
import com.storechain.common.MultiwayTreeNodeChild;
import com.storechain.interfaces.security.permission.Permissible;
import com.storechain.spring.boot.entity.EntityBase;
import com.storechain.spring.boot.entity.UserPermission;
import com.storechain.utils.DatabaseManager;

public class OwnedPermissionCollection extends AbstractSet<UserPermission> {
	
	private final Permissible permissible;
	private final Set<UserPermission> permissions;
	
	public OwnedPermissionCollection(Permissible permissible, Set<UserPermission> permissions) {
		
		this.permissible = permissible;
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
				
				Optional<UserPermission> optional = DatabaseManager.getUserPermissionRepository().findOne(Example.of((UserPermission)permission, ExampleMatcher.matching().withIgnorePaths("created_time", "last_modified_time").withIgnoreCase()));
				
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
	
	public MultiwayTreeNode<String> getTreeNode() {
		
		MultiwayTreeNode<String> root = new MultiwayTreeNode<String>(this.permissible.getIdentity());
		
		for(UserPermission permission : this.permissions) {
			
			addToParent(root, MultiwayTreeNodeChild.from(permission.getAuthority()));
		}
		
		return root;
		
	}
	
	private void addToParent(MultiwayTreeNode<String> parent, MultiwayTreeNode<String> node) {
		
		Optional<MultiwayTreeNode<String>> exist = parent.getFirstChildByValue(node.getValue());
		
		if(exist.isEmpty()) {
			((MultiwayTreeNodeChild<String>)node).unparent();
			parent.add(node);
			
		} else {
			
			for(int i = 0; i < node.size(); i++) {
				MultiwayTreeNodeChild<String> child = (MultiwayTreeNodeChild<String>) node.get(i);
				child.unparent();
				addToParent(exist.get(), child);
			}
			
		}
	}
	
	private boolean hasPermission(MultiwayTreeNode<String> root, String permission) {
		
		String[] authorities = permission.split("\\.");
		
		if(authorities != null && authorities.length > 0) {
			
			int size = root.size();
			
			for(int i = 0; i < size; i++) {
				
				MultiwayTreeNode<String> node = root.get(i);

				if(node.getValue().equals("*")) {
					
					return true;
				}
				
				if(node.getValue().equals(authorities[0])) {
					
					if(node.size() > 0 && authorities.length > 0) {
						
						return node.stream().anyMatch(child -> this.hasPermission(node, String.join(".", Arrays.copyOfRange(authorities, 1, authorities.length))));
					}
					
					if(authorities.length <= 1) {

						return true;
					}

				}
			}
			
			return false;
		}
		
		return false;
	}
	
	public boolean hasPermission(UserPermission permission) {
		
		return this.hasPermission(permission.getAuthority());
	}
	
	public boolean hasPermission(String permission) {
		
		return this.hasPermission(getTreeNode(), permission);
	}
}
