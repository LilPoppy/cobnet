package com.cobnet.spring.boot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Set<String> keys(String namespace) {

        return Objects.requireNonNull(template.keys(namespace + "::*")).stream().map(key -> key.substring((namespace + "::").length())).collect(Collectors.toSet());
    }

    public Cache get(String namespace) {

        return manager.getCache(namespace);
    }

    public <T extends Serializable> Object get(String namespace, T key) {

        Cache.ValueWrapper wrapper = Objects.requireNonNull(this.get(namespace)).get(key);

        if(wrapper == null) {

            return null;
        }

        return wrapper.get();
    }

    public <T extends Serializable, E> E get(String namespace, T key, Class<E> type) {

        return Objects.requireNonNull(this.get(namespace)).get(key, type);
    }

    public <T extends Serializable> void evict(String namespace, T key) {

        Objects.requireNonNull(this.get(namespace)).evict(key);
    }

    public <T extends Serializable> boolean evictIfPresent(String namespace, T key) {

        return Objects.requireNonNull(this.get(namespace)).evictIfPresent(key);
    }
}
