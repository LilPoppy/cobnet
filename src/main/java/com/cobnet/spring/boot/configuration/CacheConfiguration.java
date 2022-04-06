package com.cobnet.spring.boot.configuration;

import com.cobnet.cache.redis.RedisCacheErrorHandler;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;

//TODO Hotspot cache management/avalanche/penetration/hotspot/hotspot invalid
@Configuration
@EnableCaching
public class CacheConfiguration extends CachingConfigurerSupport {

    @Override
    public CacheErrorHandler errorHandler() {

        return new RedisCacheErrorHandler();
    }

    @Override
    public CacheManager cacheManager() {

        return ProjectBeanHolder.getRedisCacheManager();
    }

    @Override
    public KeyGenerator keyGenerator() {

        return ProjectBeanHolder.getRedisCacheKeyGenerator();
    }

    public RedisConfiguration getRedisConfiguration() {

        return ProjectBeanHolder.getRedisConfiguration();
    }

}
