package com.cobnet.interfaces.security;

import java.util.Collection;
import java.util.Optional;

import com.cobnet.common.MultiwayTreeNode;
import com.cobnet.common.MultiwayTreeNodeChild;
import com.cobnet.security.RoleRule;

public interface Permissible {

	 public String getIdentity();
	 
	 public RoleRule getRule();
	 
	 public Collection<? extends Permission> getPermissions();
	 
	 public default boolean isPermitted(Permission permission) {
		 
		 return this.isPermitted(permission.getAuthority());
	 }
	 
	 public boolean isPermitted(String authority);
	 
	 public boolean addPermission(Permission permission);
	 
	 public boolean removePermission(Permission permission);

	 default MultiwayTreeNode<String> getTreeNode() {

		MultiwayTreeNode<String> root = new MultiwayTreeNode<String>(this.getIdentity());

		for(Permission permission : this.getPermissions()) {

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
}
