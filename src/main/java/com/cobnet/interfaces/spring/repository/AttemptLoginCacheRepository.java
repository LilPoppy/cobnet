package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.cache.AttemptLoginCache;
import org.springframework.stereotype.Repository;

@Repository
public interface AttemptLoginCacheRepository extends IndexedRedisRepository<AttemptLoginCache, String> {
}
