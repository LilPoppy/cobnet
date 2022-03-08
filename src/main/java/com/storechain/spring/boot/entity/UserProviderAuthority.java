package com.storechain.spring.boot.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.storechain.interfaces.security.permission.Permission;

import lombok.Data;
import reactor.util.annotation.NonNull;

@Entity
@Data
public class UserProviderAuthority extends EntityBase implements Permission, Serializable  {


	private static final long serialVersionUID = -1424826140778795160L;
	
	@Id
	private String name;
	
	public UserProviderAuthority() {}
	
	public UserProviderAuthority(@NonNull String name) {
		
		this.name = name;
	}

	@Override
	public String getAuthority() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj instanceof SimpleGrantedAuthority) {
			return this.name.equals(((SimpleGrantedAuthority) obj).getAuthority());
		}

		if (obj instanceof UserProviderAuthority) {

			return this.name.equals(((UserPermission) obj).getAuthority());
		}

		return false;
	}
	
	@Override
	public int hashCode() {

		return this.name.hashCode();
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public int getPower() {
		
		return 0;
	}
}
