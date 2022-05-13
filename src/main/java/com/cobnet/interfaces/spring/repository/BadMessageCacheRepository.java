package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.cache.BadMessageCache;
import org.springframework.stereotype.Repository;

@Repository
public interface BadMessageCacheRepository extends IndexedRedisRepository<BadMessageCache, String> {
}
