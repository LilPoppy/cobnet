package com.storechain.spring.boot.entity;

import lombok.Data;

import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Entity
public class User implements UserDetails {

	@Id
    private String username;

    private String password;
    
    @ManyToOne(targetEntity = UserGrantedAuthorities.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "authorities")
    private UserGrantedAuthorities authorities;
    
    private boolean expired;
    
    private boolean locked;
    
    private boolean vaildPassword;
    
    private boolean enabled;

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
	public Collection<GrantedAuthority> getAuthorities() {
		
		return (Collection<GrantedAuthority>) authorities.getAuthorities();
	}

	public void setAuthorities(UserGrantedAuthorities authorities) {
		
		this.authorities = authorities;
		
	}
	
	public static User fromData(String username, String password, UserGrantedAuthorities authorities, boolean expired, boolean locked, boolean vaildPassword, boolean enabled) {
		
		return new User() {{ 
			setUsername(username);
			setPassword(password);
			setAuthorities(authorities);
			setExpired(expired);
			setLocked(locked);
			setVaildPassword(vaildPassword);
			setEnabled(enabled);
		}};
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
}
