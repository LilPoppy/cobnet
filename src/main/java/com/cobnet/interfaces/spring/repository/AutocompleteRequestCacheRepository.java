package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.cache.AutocompleteRequestCache;
import org.springframework.stereotype.Repository;

@Repository
public interface AutocompleteRequestCacheRepository extends IndexedRedisRepository<AutocompleteRequestCache, String> {
}
