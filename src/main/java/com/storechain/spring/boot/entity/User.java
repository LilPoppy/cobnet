package com.storechain.spring.boot.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
public class User implements UserDetails {

	@Id
    private String username;

	@Column(nullable = false)
    private String password;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_authorities", joinColumns = { @JoinColumn(name = "user", referencedColumnName = "username") },
            inverseJoinColumns = {@JoinColumn(name = "authority", referencedColumnName = "authority")})
    private Collection<UserAuthority> authorities = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_permissions", joinColumns = { @JoinColumn(name = "user", referencedColumnName = "username") },
    		inverseJoinColumns = {@JoinColumn(name = "permissions", referencedColumnName = "name") })
    private Collection<UserPermission> permissions = new ArrayList<>();

    private boolean expired;
    
    private boolean locked;
    
    private boolean vaildPassword;
    
    private boolean enabled;
    
    public User() {}
    
    public User(String username, String password, Collection<? extends GrantedAuthority> authorities, boolean expired, boolean locked, boolean vaildPassword, boolean enabled) {
    	
    	this.username = username;
    	this.password = password;
    	setAuthorities(authorities);
    	this.expired = expired;
    	this.locked = locked;
    	this.vaildPassword = vaildPassword;
    	this.enabled = enabled;
    }
    
    public User(String username, String password, Collection<? extends GrantedAuthority> authorities) {
    	
    	this(username, password, authorities, false, false, true, true);
    }
    
    public User(String username, String password, GrantedAuthority... authorities) {
    	
    	this(username, password, Arrays.stream(authorities).toList());
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

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return authorities;
	}


	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		
		this.authorities = authorities.stream().map(role -> new UserAuthority(role.getAuthority())).toList();
	}
	
	public <T extends GrantedAuthority> void setAuthorities(T... authorities) {
		
		this.setAuthorities(Arrays.asList(authorities));
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

	public Collection<UserPermission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Collection<UserPermission> permissions) {
		this.permissions = permissions;
	}
}
