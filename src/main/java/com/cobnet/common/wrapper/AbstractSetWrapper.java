package com.cobnet.common.wrapper;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractSetWrapper<T> extends AbstractSet<T> {

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
