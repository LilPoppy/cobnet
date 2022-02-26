package com.storechain.spring.boot.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import lombok.Data;

@Data
@Entity
@Table(name = "user-authority")
public class UserGrantedAuthority implements GrantedAuthority {

	@Id
	private String authority;
	
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
	
	public static UserGrantedAuthority fromData(String role) {
		
		return new UserGrantedAuthority() {{
			
			setAuthority(role);
		}};
	}

}
