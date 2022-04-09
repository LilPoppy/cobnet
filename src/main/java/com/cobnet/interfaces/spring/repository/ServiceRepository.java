package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ServiceRepository extends JPABaseRepository<Service, Long> {
}
