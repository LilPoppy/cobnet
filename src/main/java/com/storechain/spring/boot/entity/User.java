package com.storechain.spring.boot.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.storechain.interfaces.security.permission.Permissible;
import com.storechain.interfaces.security.permission.Permission;
import com.storechain.security.OperatorRole;
import com.storechain.security.permission.OwnedPermissionCollection;
import com.storechain.utils.DatabaseManager;

import reactor.util.annotation.NonNull;

@Entity
public class User implements UserDetails, Permissible {

	@Id
    private String username;

	@Column(nullable = false)
    private String password;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = { @JoinColumn(name = "user", referencedColumnName = "username") },
            inverseJoinColumns = {@JoinColumn(name = "role", referencedColumnName = "role")})
    private List<UserRole> roles = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_permissions", joinColumns = { @JoinColumn(name = "user", referencedColumnName = "username") },
    		inverseJoinColumns = {@JoinColumn(name = "permissions", referencedColumnName = "name") })
    private List<UserPermission> permissions = new ArrayList<UserPermission>();

    private boolean expired;
    
    private boolean locked;
    
    private boolean vaildPassword;
    
    private boolean enabled;
    
    public User() {}
    
    public User(@NonNull String username, @NonNull String password, @NonNull List<UserRole> roles, boolean expired, boolean locked, boolean vaildPassword, boolean enabled) {
    	
    	this.username = username;
    	this.password = password;
    	this.roles = new ArrayList<>(roles);
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

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public void setRoles(Collection<? extends Permissible> authorities) {
		
		this.roles = authorities.stream().map(role -> DatabaseManager.getUserRoleRepository().findByRoleIgnoreCase(role.getIdentity())).toList();
	}
	
	public <T extends Permissible> void setAuthorities(T... authorities) {
		
		this.setRoles(Arrays.asList(authorities));
	}
	
	public Collection<? extends UserRole> getRoles() {
		
		return Collections.unmodifiableCollection(roles);
	}
	
	public Collection<? extends UserPermission> getPermissions() {
		//TODO combin all roles and self permissions
		return Collections.unmodifiableCollection(permissions);
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
		
		return super.toString() + "(" + this.username + ")";
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
	public boolean hasPermitted(String authority) {

		return this.getPermissions().stream().anyMatch(permission -> permission.compareTo(authority) >= 0);
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
