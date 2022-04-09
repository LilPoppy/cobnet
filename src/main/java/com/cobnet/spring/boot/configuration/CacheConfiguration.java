package com.cobnet.spring.boot.configuration;

import com.cobnet.cache.redis.RedisCacheErrorHandler;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;

//TODO Hotspot cache management/avalanche/penetration/hotspot/hotspot invalid
@Configuration
@ConfigurationProperties("spring.cache")
@EnableCaching
public class CacheConfiguration extends CachingConfigurerSupport {

    private boolean startClear;

    public boolean isStartClear() {
        return startClear;
    }

    public void setStartClear(boolean startClear) {
        this.startClear = startClear;
    }

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
