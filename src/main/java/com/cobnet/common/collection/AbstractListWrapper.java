package com.cobnet.common.collection;

import java.util.AbstractList;
import java.util.List;

public abstract class AbstractListWrapper<T> extends AbstractList<T> {

	@Override
    public void add(int index, T element) {
        this.getList().add(index, element);
    }
    
	@Override
    public T remove(int index) {
        return this.getList().remove(index);
    }
	
	@Override
    public T set(int index, T element) {
        return this.getList().set(index, element);
    }
    
	
	@Override
	public T get(int index) {
		
		return this.getList().get(index);
	}

	@Override
	public int size() {

		return this.getList().size();
	}
	
    protected abstract List<T> getList();


}
