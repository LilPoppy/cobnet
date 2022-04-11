package com.cobnet.spring.boot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

@Service
public class CacheService {

    @Autowired
    private RedisCacheManager manager;

    @Autowired
    private RedisTemplate<String, Object> template;

    public <T extends Serializable, E> boolean set(String namespace, T key, E value, Duration timeout) {

        Objects.requireNonNull(manager.getCache(namespace)).put(key, value);

        return Boolean.TRUE.equals(template.expire(namespace + "::" + key, timeout));
    }

    public <T extends Serializable> Object get(String namespace, T key) {

        Cache.ValueWrapper wrapper = Objects.requireNonNull(manager.getCache(namespace)).get(key);

        if(wrapper == null) {

            return null;
        }

        return wrapper.get();
    }

    public <T extends Serializable, E> E get(String namespace, T key, Class<E> type) {

        return Objects.requireNonNull(manager.getCache(namespace)).get(key, type);
    }

    public <T extends Serializable> void evict(String namespace, T key) {

        Objects.requireNonNull(manager.getCache(namespace)).evict(key);
    }

    public <T extends Serializable> boolean evictIfPresent(String namespace, T key) {

        return Objects.requireNonNull(manager.getCache(namespace)).evictIfPresent(key);
    }
}
