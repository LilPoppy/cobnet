package com.storechain.common;

import java.util.Iterator;
import java.util.Set;

public abstract class AbstractSet<T> extends java.util.AbstractSet<T> {

	@Override
	public Iterator<T> iterator() {
		return this.getSet().iterator();
	}

	@Override
	public int size() {
		return this.getSet().size();
	}
	
	@Override
    public boolean add(T e) {
		
		return this.getSet().add(e);
    }
	
	protected abstract Set<T> getSet();

}
