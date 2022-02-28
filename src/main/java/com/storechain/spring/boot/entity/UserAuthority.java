package com.storechain.spring.boot.entity;

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.storechain.utils.SpringContext;

@Entity
public class UserAuthority implements GrantedAuthority {

	@Id
	private String authority;
	
	private byte power;
	
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "authority_permissions", joinColumns = { @JoinColumn(name = "authority", referencedColumnName = "authority")},
	            inverseJoinColumns = {@JoinColumn(name = "permissions", referencedColumnName = "name")})
    private Collection<UserPermission> permissions = new ArrayList<>();
	
	public UserAuthority() {}
	
	public UserAuthority(String authority, byte power) {
		
		this.authority = authority;
		this.power = power;
	}
	
	public UserAuthority(String authority) {
		
		this(authority, SpringContext.getSecurityConfiguration().getAuthorityDefaultPower());
	}

	@Override
	public String getAuthority() {
		
		return this.authority;
	}

	public void setAuthority(String authority) {
		
		this.authority = authority;
	}
	
	public byte getPower() {
		return power;
	}

	public void setPower(byte power) {
		this.power = power;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) {
			
			return true;
		}
		
		if (obj instanceof SimpleGrantedAuthority) {
			return this.authority.equals(((SimpleGrantedAuthority) obj).getAuthority());
		}
		
		if(obj instanceof UserAuthority) {
			
			return this.authority.equals(((UserAuthority)obj).getAuthority());
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		
		return this.authority.hashCode();
	}

	@Override
	public String toString() {
		
		return this.authority;
	}


}
