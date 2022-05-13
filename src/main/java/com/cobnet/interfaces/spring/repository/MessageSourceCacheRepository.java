package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.cache.MessageSourceCache;
import org.springframework.stereotype.Repository;

import java.util.Locale;

@Repository
public interface MessageSourceCacheRepository extends RedisRepository<MessageSourceCache, Locale> {
}
