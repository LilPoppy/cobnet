package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.Store;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JPABaseRepository<Store, Long> {
}
