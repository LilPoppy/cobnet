package com.cobnet.interfaces.spring.cache;

public interface IndexedCacheEntity<ID> {

    public boolean isIndexed(Object object);

    public ID resolve(Object object);
}
