package com.storechain.security;

public enum OperatorRole {
	
	VISITOR(0),
	USER(1),
	SERVER(2),
	ADMIN(3);
	
	private final int level;
	
	private OperatorRole(int level) {
		
		this.level = level;
	}
	
	public int getLevel() {
		
		return this.level;
	}
}
