package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.Store;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface StoreRepository extends JPABaseRepository<Store, String> {

    @Cacheable(cacheNames = "Stores", unless="#result == null")
    public Optional<Store> findById(String id);


}
