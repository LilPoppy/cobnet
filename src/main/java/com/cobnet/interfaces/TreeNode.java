package com.cobnet.interfaces;

public interface TreeNode<T> {
	
	public default boolean isRoot() {
		
		return this.getLevel() == 0;
	}
	
	public int getLevel();
}
