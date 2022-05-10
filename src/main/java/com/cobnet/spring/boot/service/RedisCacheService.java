package com.cobnet.spring.boot.service;

import com.cobnet.interfaces.CacheValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RedisCacheService {

    private static Map<Class<? extends CacheValue>, Map<String, String>> NAMESPACES = new HashMap<>();

    @Autowired
    private RedisCacheManager manager;

    @Autowired
    private RedisTemplate<String, Object> template;

    public <T extends Serializable, E extends CacheValue> E transfer(Class<?> namespace, Class<E> type, T oldKey, T newKey) {

        return (E) this.transfer(this.getNameSpace(type, namespace), oldKey, newKey);
    }

    public <T extends Serializable, E extends CacheValue> E transfer(String namespace, Class<E> type, T oldKey, T newKey) {

        return (E) this.transfer(this.getNameSpace(type, namespace), oldKey, newKey);
    }

    public <T extends Serializable> Object transfer(String namespace, T oldKey, T newKey) {

        Object cache = this.get(namespace, oldKey);

        if(cache == null) {

            return null;
        }

        Duration timeout = Duration.ofSeconds(template.getExpire(this.getKey(namespace, oldKey)));

        boolean result = this.evictIfPresent(namespace, oldKey);

        if(this.set(namespace, newKey, cache, timeout) && result) {

            return cache;
        }
        return null;
    }

    public <T extends Serializable, E extends CacheValue> boolean set(Class<?> namespace, T key, E value, Duration timeout) {

        return this.set(this.getNameSpace(value.getClass(), namespace), key, (Object) value, timeout);
    }

    public <T extends Serializable, E extends CacheValue> boolean set(String namespace, T key, E value, Duration timeout) {

        return this.set(this.getNameSpace(value.getClass(), namespace), key, (Object) value, timeout);
    }

    public <T extends Serializable, E> boolean set(String namespace, T key, E value, Duration timeout) {

        this.get(namespace).put(key, value);

        if(timeout.isZero()) {

            return true;
        }

        return Boolean.TRUE.equals(template.expire(getKey(namespace, key), timeout));
    }

    public <T extends CacheValue> Set<String> keys(Class<?> namespace, Class<T> type) {

        return this.keys(this.getNameSpace(type, namespace));
    }

    public <T extends CacheValue> Set<String> keys(String namespace, Class<T> type) {

        return this.keys(this.getNameSpace(type, namespace));
    }

    public Set<String> keys(String namespace) {

        return Objects.requireNonNull(template.keys(getKey(namespace, "*"))).stream().map(key -> key.substring(namespace.length() + 2)).collect(Collectors.toSet());
    }

    public <T extends CacheValue> Cache get(Class<?> namespace, Class<T> type) {

        return this.get(this.getNameSpace(type, namespace));
    }

    public <T extends CacheValue> Cache get(String namespace, Class<T> type) {

        return this.get(this.getNameSpace(type, namespace));
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

    public <T extends Serializable, E extends CacheValue> E get(String namespace, Class<E> type, T key) {

        return this.get(this.getNameSpace(type, namespace), key, type);
    }

    public <T extends Serializable, E extends CacheValue> E get(Class<?> namespace, Class<E> type, T key) {

        return this.get(this.getNameSpace(type, namespace), key, type);
    }

    public <T extends Serializable, E> E get(String namespace, T key, Class<E> type) {

        return Objects.requireNonNull(this.get(namespace)).get(key, type);
    }

    public <T extends Serializable, E extends CacheValue> void evict(String namespace, Class<E> type, T key) {

        this.evict(this.getNameSpace(type, namespace), key);
    }

    public <T extends Serializable, E extends CacheValue> void evict(Class<?> namespace, Class<E> type, T key) {

        this.evict(this.getNameSpace(type, namespace), key);
    }

    public <T extends Serializable> void evict(String namespace, T key) {

        Objects.requireNonNull(this.get(namespace)).evict(key);
    }

    public <T extends Serializable, E extends CacheValue> boolean evictIfPresent(String namespace, Class<E> type, T key) {

        return this.evictIfPresent(this.getNameSpace(type, namespace), key);
    }

    public <T extends Serializable, E extends CacheValue> boolean evictIfPresent(Class<?> namespace, Class<E> type, T key) {

        return this.evictIfPresent(this.getNameSpace(type, namespace), key);
    }

    public <T extends Serializable> boolean evictIfPresent(String namespace, T key) {

        return Objects.requireNonNull(this.get(namespace)).evictIfPresent(key);
    }

    public <T extends CacheValue, E extends Serializable> String getKey(Class<T> type, Class<?> namespace, E key) {

        return getKey(getNameSpace(type, namespace), key);
    }

    public <T extends Serializable> String getKey(String namespace, T key) {

        return new StringBuilder(namespace).append("::").append(new String(Objects.requireNonNull(new GenericToStringSerializer<>((Class<T>)key.getClass()).serialize(key)))).toString();
    }

    public <T extends CacheValue> String getNameSpace(Class<T> type, Class<?> namespace) {

        return this.getNameSpace(type, namespace.getSimpleName());
    }

    public <T extends CacheValue> String getNameSpace(Class<T> type, String namespace) {

        Map<String, String> namespaces = RedisCacheService.NAMESPACES.get(type);

        if(namespaces == null) {

            namespaces = new HashMap<>();
            RedisCacheService.NAMESPACES.put(type, namespaces);
        }

        String result = namespaces.get(namespace);

        if(result == null) {

            result = new StringBuilder(type.getSimpleName()).append(":").append(namespace).toString();
            namespaces.put(namespace, result);
        }

        return result;
    }
}
