package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.cache.HumanValidationCache;
import org.springframework.stereotype.Repository;

@Repository
public interface HumanValidationCacheRepository extends IndexedRedisRepository<HumanValidationCache, String> {



}
