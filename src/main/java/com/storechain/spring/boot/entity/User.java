package com.storechain.spring.boot.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.storechain.common.MultiwayTreeNode;
import com.storechain.interfaces.security.permission.Permissible;
import com.storechain.interfaces.security.permission.Permission;
import com.storechain.security.OperatorRole;
import com.storechain.security.OwnedRoleCollection;
import com.storechain.security.permission.OwnedPermissionCollection;
import reactor.util.annotation.NonNull;

@Entity
public final class User extends EntityBase implements UserDetails, Permissible {

	@Id
    private String username;

	@Column(nullable = false)
    private String password;
    
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "user_roles", joinColumns = { @JoinColumn(name = "user", referencedColumnName = "username") },
            inverseJoinColumns = { @JoinColumn(name = "role", referencedColumnName = "role") })
    private Set<UserRole> roles = new HashSet<>();
    
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "user_permissions", joinColumns = { @JoinColumn(name = "user", referencedColumnName = "username") },
    		inverseJoinColumns = { @JoinColumn(name = "permissions", referencedColumnName = "authority") })
    private Set<UserPermission> permissions = new HashSet<UserPermission>();

    private boolean expired;
    
    private boolean locked;
    
    private boolean vaildPassword;
    
    private boolean enabled;
    
    public User() {}
    
    public User(@NonNull String username, @NonNull String password, @NonNull List<UserRole> roles, boolean expired, boolean locked, boolean vaildPassword, boolean enabled) {
    	
    	this.username = username;
    	this.password = password;
    	this.getOwnedRoleCollection().addAll(roles.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>((x, y) -> ((UserRole)x).getCreatedTime().compareTo(((UserRole)y).getCreatedTime()))), ArrayList::new)));
    	this.expired = expired;
    	this.locked = locked;
    	this.vaildPassword = vaildPassword;
    	this.enabled = enabled;
    	
    }
    
    public User(@NonNull String username, @NonNull String password, @NonNull List<UserRole> roles) {
    	
    	this(username, password, roles, false, false, true, true);
    }
    
    public User(@NonNull String username, @NonNull String password, UserRole... roles) {
    	
    	this(username, password, Arrays.stream(roles).toList());
    }
    

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public void setRoles(Collection<? extends UserRole> roles) {

		this.getOwnedRoleCollection().clear();;
		this.getOwnedRoleCollection().addAll(roles);
	}
	
	public <T extends UserRole> void setRoles(T... roles) {
		
		this.setRoles(Arrays.asList(roles));
	}
	
	public Set<? extends UserRole> getRoles() {
		
		return Collections.unmodifiableSet(roles);
	}
	
	public Set<? extends UserPermission> getPermissions() {
		
		return Stream.of(roles.stream().map(role -> role.getPermissions()).flatMap(Collection::stream).collect(Collectors.toList()), permissions).flatMap(Collection::stream).distinct().collect(Collectors.toUnmodifiableSet());
	}
	
	public OwnedRoleCollection getOwnedRoleCollection() {
		
		return new OwnedRoleCollection(roles);
	}
	
	public OwnedPermissionCollection getOwnedPermissionCollection() {
		
		return new OwnedPermissionCollection(permissions);
	}

	@Override
	public boolean isAccountNonExpired() {
		return !this.expired;
	}

	@Override
	public boolean isAccountNonLocked() {
		
		return !this.locked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		
		return !this.vaildPassword;
	}

	@Override
	public boolean isEnabled() {
		
		return this.enabled;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean exipired) {
		this.expired = exipired;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isVaildPassword() {
		return vaildPassword;
	}

	public void setVaildPassword(boolean vaildPassword) {
		this.vaildPassword = vaildPassword;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@Override
	public String toString() {
		
		return super.toString() + "(" + this.username + ")" + "[" + this.roles + "]";
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return this.getPermissions();
	}

	@Override
	public OperatorRole getOperatorRole() {
		
		Optional<UserRole> role = this.roles.stream().max((x, y) -> Integer.compare(x.getPower(), y.getPower()));
		
		return role.isEmpty() ? OperatorRole.VISITOR : role.get().getOperatorRole();
	}

	@Override
	public String getIdentity() {
		
		return this.username;
	}

	@Override
	public boolean isPermitted(String authority) {

		return Stream.of(this.getOwnedRoleCollection().stream().map(collection -> collection.getOwnedPermissionCollection()).toList(), this.getOwnedPermissionCollection()).anyMatch(collection -> ((OwnedPermissionCollection)collection).hasPermission(authority));
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
