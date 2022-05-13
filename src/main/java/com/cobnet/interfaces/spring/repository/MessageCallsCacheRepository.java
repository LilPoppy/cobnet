package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.cache.MessageCallsCache;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageCallsCacheRepository extends IndexedRedisRepository<MessageCallsCache, String> {
}
