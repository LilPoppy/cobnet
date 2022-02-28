package com.storechain.spring.boot.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.storechain.interfaces.security.permission.Permissible;
import com.storechain.interfaces.security.permission.Permission;
import com.storechain.security.OperatorRole;
import com.storechain.security.permission.OwnedPermissionCollection;
import com.storechain.utils.SpringContext;

import reactor.util.annotation.NonNull;

@Entity
public class UserRole implements Permissible {

	@Id
	private String role;
	
	private OperatorRole rule;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "role_permissions", joinColumns = {
			@JoinColumn(name = "role", referencedColumnName = "role") }, inverseJoinColumns = {
					@JoinColumn(name = "permissions", referencedColumnName = "name") })
	private List<UserPermission> permissions = new ArrayList<UserPermission>();

	public UserRole() {
	}

	public UserRole(@NonNull String role, OperatorRole rule) {

		this.role = role;
		this.rule = rule;
	}
	
	public UserRole(@NonNull String role) {
		
		this(role, OperatorRole.USER);
	}
	
	public String getRole() {

		return this.role;
	}

	public int getPower() {
		
		return this.getPermissions().stream().map(permission -> permission.getPower()).mapToInt(Integer::valueOf).sum();
	}
	
	public List<? extends UserPermission> getPermissions() {
		
		return Collections.unmodifiableList(permissions);
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
	public String toString() {

		return this.role;
	}

	@Override
	public OperatorRole getOperatorRole() {
		return this.rule;
	}

	@Override
	public boolean hasPermitted(String authority) {
		
		return permissions.stream().anyMatch(permission -> permission.compareTo(authority) >= 0);
	}

	@Override
	public <T extends Permission> void addPermission(T permission) {
		
		this.getOwnedPermissionCollection().add(permission);
		
	}

	@Override
	public <T extends Permission> void removePermission(T permission) {
		
		this.getOwnedPermissionCollection().remove(permission);
	}

}
