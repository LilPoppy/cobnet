package com.storechain.common;



public class MultiwayTreeNode<T extends Comparable<T>> extends MultiwayTree<T> {

	protected MultiwayTree<T> parent;
	
	public MultiwayTreeNode(T value, MultiwayTree<T> parent) {
		super(value);
		this.parent = parent;
	}
	
	public MultiwayTreeNode(T value) {
		this(value, null);
	}
	
	@Override
	public int getLevel() {
		
		if(this.parent == null) {
			
			return 0;
		}

		return this.parent.getLevel() + 1;
	}
	
	public MultiwayTree<T> getParent() {
		
		return this.parent;
	}
	
}