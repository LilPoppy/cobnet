package com.storechain.spring.boot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.storechain.common.MultiwayTree;
import com.storechain.interfaces.security.permission.Permission;
import com.storechain.utils.SpringContext;

import reactor.util.annotation.NonNull;


@Entity
public class UserPermission implements Permission {

    @Id
    private String name;
    
    @Column(nullable = false)
    private String authority;
    
    private String description;
    
    private int power;
    
    public UserPermission() {}
    
    public UserPermission(@NonNull String name, @NonNull String authority, String description, int power) {
    	
    	this.name = name;
    	this.authority = authority;
    	this.description = description;
    	this.power = power;
    }
    
    public UserPermission(@NonNull String name, @NonNull String authority, int power) {
    	
    	this(name, authority, "", power);
    }
    
    public UserPermission(@NonNull String name, @NonNull String authority, String description) {
    	
    	this(name, authority, description, SpringContext.getSecurityConfiguration().getAuthorityDefaultPower());
    }
    
    public UserPermission(@NonNull String name, @NonNull String authority) {
    	
    	this(name, authority, "");
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getAuthority() {
		return this.authority;
	}

	@Override
	public String getDescription() {
		return this.description;
	}
	
	@Override
	public int getPower() {

		return this.power;
	}
	
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj instanceof SimpleGrantedAuthority) {
			return this.authority.equals(((SimpleGrantedAuthority) obj).getAuthority());
		}

		if (obj instanceof UserPermission) {

			return this.authority.equals(((UserPermission) obj).getAuthority());
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
	
	@Override
	public int compareTo(String authority) {

		//TODO 
		return MultiwayTree.from(this.authority).compareTo(MultiwayTree.from(authority));
	}

}
