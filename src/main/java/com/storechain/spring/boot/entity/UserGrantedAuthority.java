package com.storechain.spring.boot.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Entity
public class UserGrantedAuthority implements GrantedAuthority {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String authority;
	
	public UserGrantedAuthority() {}
	
	public UserGrantedAuthority(String authority) {
		
		this.authority = authority;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String getAuthority() {
		
		return this.authority;
	}

	public void setAuthority(String authority) {
		
		this.authority = authority;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) {
			
			return true;
		}
		
		if (obj instanceof SimpleGrantedAuthority) {
			return this.authority.equals(((SimpleGrantedAuthority) obj).getAuthority());
		}
		
		if(obj instanceof UserGrantedAuthority) {
			
			return this.authority.equals(((UserGrantedAuthority)obj).getAuthority());
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
