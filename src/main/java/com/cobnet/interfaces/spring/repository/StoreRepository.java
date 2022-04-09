package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.Store;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface StoreRepository extends JPABaseRepository<Store, Long> {

    @Cacheable("Stores")
    public Store findById(long id);
}
