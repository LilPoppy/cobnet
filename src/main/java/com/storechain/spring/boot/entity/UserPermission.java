package com.storechain.spring.boot.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserPermission {

    @Id
    private String name;
    
    private String node;
    
    public UserPermission() {}
    
    public UserPermission(String name, String node) {
    	
    	this.name = name;
    	this.node = node;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}
}
