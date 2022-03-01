package com.storechain.interfaces;

public interface TreeNode<T extends Comparable<T>> {
	
	public default boolean isRoot() {
		
		return this.getLevel() == 0;
	}
	
	public int getLevel();
}
