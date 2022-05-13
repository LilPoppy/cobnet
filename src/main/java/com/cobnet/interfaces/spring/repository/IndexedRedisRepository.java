package com.cobnet.interfaces.spring.repository;


import com.cobnet.interfaces.spring.cache.IndexedCacheEntity;
import com.cobnet.spring.boot.core.ProjectBeanHolder;

import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface IndexedRedisRepository<T extends IndexedCacheEntity<ID>, ID> extends RedisRepository<T, ID>{

    public static void match(Object index, BiConsumer<IndexedRedisRepository, IndexedCacheEntity<?>> callback) {

        String[] names = ProjectBeanHolder.getSpringContext().getBeanNamesForType(IndexedRedisRepository.class);

        for(String name : names) {

            IndexedRedisRepository repository = ProjectBeanHolder.getSpringContext().getBean(name, IndexedRedisRepository.class);

            Iterator<IndexedCacheEntity> it = repository.findAll().iterator();

            while(it.hasNext()) {

                IndexedCacheEntity current = it.next();

                if(current != null) {

                    if (current.isIndexed(index)) {

                        callback.accept(repository, current);
                    }
                }
            }
        }
    }

    public static void updateIndex(Object oldIndex, Object newIndex) {

        match(oldIndex, (repository, entity) -> repository.key(entity, entity.resolve(newIndex)));
    }

    public static void deleteAll(Object index) {

        match(index, (repository, entity) -> repository.delete(entity));
    }
}
