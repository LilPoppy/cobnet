package com.storechain.spring.boot.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.storechain.common.MultiwayTreeNode;
import com.storechain.interfaces.security.permission.Permission;
import com.storechain.utils.SpringContext;

import reactor.util.annotation.NonNull;

@Entity
public final class UserPermission extends EntityBase implements Permission {

    @Id
    private String authority;
    
    private String description;
    
    private int power;
    
    public UserPermission() {}
    
    public UserPermission(@NonNull String authority, String description, int power) {
    	this.authority = authority;
    	this.description = description;
    	this.power = power;
    }
    
    public UserPermission(@NonNull String authority, String description) {
    	
    	this(authority, description, SpringContext.getSecurityConfiguration().getPermissionDefaultPower());
    }
    
    public UserPermission(@NonNull String authority, int power) {
    	
    	this(authority, "", power);
    }
    
    public UserPermission(@NonNull String authority) {
    	
    	this(authority, "");
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

		return 0;//MultiwayTreeNode.from("permission." + this.authority).contains(MultiwayTreeNode.from(authority), "*") ? 0 : -1;
	}

}
