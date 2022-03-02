package com.storechain.spring.boot.entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import com.storechain.common.MultiwayTreeNode;
import com.storechain.interfaces.security.permission.Permissible;
import com.storechain.interfaces.security.permission.Permission;
import com.storechain.security.OperatorRole;
import com.storechain.security.permission.OwnedPermissionCollection;
import reactor.util.annotation.NonNull;

@Entity
public final class UserRole extends EntityBase implements Permissible {

	@Id
	private String role;
	
	private OperatorRole rule;

	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "role_permissions", joinColumns = {
			@JoinColumn(name = "role", referencedColumnName = "role") }, inverseJoinColumns = {
					@JoinColumn(name = "permissions", referencedColumnName = "authority") })
	private Set<UserPermission> permissions = new HashSet<UserPermission>();

	public UserRole() {
	}

	public UserRole(@NonNull String role, OperatorRole rule) {

		this.role = role;
		this.rule = rule;
	}
	
	public UserRole(@NonNull String role, OperatorRole rule, UserPermission... permissions) {
		
		this(role, rule);
		
		this.getOwnedPermissionCollection().add(permissions);
	}
	
	public UserRole(@NonNull String role) {
		
		this(role, OperatorRole.USER);
	}
	
	public UserRole(@NonNull String role, UserPermission... permissions) {
		
		this(role);
		
		this.getOwnedPermissionCollection().add(permissions);
	}
	
	public String getRole() {

		return this.role;
	}

	public int getPower() {
		
		return this.getPermissions().stream().map(permission -> permission.getPower()).mapToInt(Integer::valueOf).sum();
	}
	
	public Set<? extends UserPermission> getPermissions() {
		
		return Collections.unmodifiableSet(permissions);
	}

	public OwnedPermissionCollection getOwnedPermissionCollection() {

		return new OwnedPermissionCollection(this.permissions);
	}

	@Override
	public String getIdentity() {

		return this.role;
	}
	
	@Override
	public int hashCode() {

		return this.role.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj instanceof UserRole) {

			return this.role.equals(((UserRole) obj).getRole());
		}

		return false;
	}

	@Override
	public String toString() {

		return "[" + this.role + "," + this.rule.toString() + "]";
	}
	
	public int getLevel() {
		return this.getOperatorRole().getLevel();
	}

	@Override
	public OperatorRole getOperatorRole() {
		return this.rule;
	}

	@Override
	public boolean isPermitted(String authority) {
		
		MultiwayTreeNode<String> root = new MultiwayTreeNode<String>(this.role);
		
		for(Permission permission : this.getPermissions()) {
			
			root.add(MultiwayTreeNode.from(permission.getAuthority()));
		}
		
		return root.contains(MultiwayTreeNode.from(authority));
	}

	@Override
	public <T extends Permission> void addPermission(T permission) {
		
		this.getOwnedPermissionCollection().add((UserPermission)permission);
		
	}

	@Override
	public <T extends Permission> void removePermission(T permission) {
		
		this.getOwnedPermissionCollection().remove(permission);
	}

}
