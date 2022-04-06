package com.cobnet.spring.boot.aspect;

import com.cobnet.interfaces.cache.CacheKeyProvider;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class CacheEvictAspect {

    private static Logger LOG = LoggerFactory.getLogger(CacheEvictAspect.class);

    @Around("@annotation(com.cobnet.interfaces.cache.annotation.SimpleCacheEvict)")
    public Object processMethodsAnnotatedWithCacheEvictAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Method method = signature.getMethod();

        CacheEvict annotation = method.getAnnotation(CacheEvict.class);

        if(joinPoint.getTarget() instanceof CacheKeyProvider<?> provider) {

            for(String name : annotation.cacheNames()) {

                for(Object key : provider.getKeys()) {

                    ProjectBeanHolder.getRedisCacheManager().getCache(name).evictIfPresent(key);
                }
            }

            return joinPoint.proceed(joinPoint.getArgs());
        }

        return joinPoint.proceed(joinPoint.getArgs());
    }
}
