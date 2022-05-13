package com.cobnet.spring.boot.cache;

import com.cobnet.interfaces.spring.cache.IndexedCacheEntity;
import org.springframework.data.annotation.Id;

public abstract class AbstractIndexedCache<ID> implements IndexedCacheEntity<ID> {

    @Id
    private ID id;

    protected AbstractIndexedCache() {}

    protected AbstractIndexedCache(ID id) {
        this.id = id;
    }

    public ID getId() {
        return id;
    }

    @Override
    public boolean isIndexed(Object object) {

        ID id = this.resolve(object);

        return id != null && id.equals(id);
    }
}
