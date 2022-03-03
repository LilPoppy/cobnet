package com.storechain.common;

public final class MultiwayTreeNodeChild<T> extends MultiwayTreeNode<T> {

	protected MultiwayTreeNode<T> parent;
	
	public MultiwayTreeNodeChild(T value, MultiwayTreeNode<T> parent) {
		super(value);
		this.parent = parent;
	}
	
	public MultiwayTreeNodeChild(T value) {
		this(value, null);
	}
	
	@Override
	public int getLevel() {
		
		if(this.parent == null) {
			
			return 0;
		}

		return this.parent.getLevel() + 1;
	}
	
	
	public MultiwayTreeNode<T> getParent() {
		
		return this.parent;
	}
	
	public void unparent() {
		
		if(this.parent != null) {
			
			this.parent.remove(this);
			this.parent = null;
		}
	}
	
}