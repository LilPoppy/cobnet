package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.cache.GoogleMapRequestCache;
import org.springframework.stereotype.Repository;

@Repository
public interface AutocompleteRequestCacheRepository extends IndexedRedisRepository<GoogleMapRequestCache, String> {
}
