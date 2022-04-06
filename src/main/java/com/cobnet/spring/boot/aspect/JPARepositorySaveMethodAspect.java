package com.cobnet.spring.boot.aspect;

import com.cobnet.spring.boot.core.ProjectBeanHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

@Aspect
@Component
public class JPARepositorySaveMethodAspect {

    private static Logger LOG = LoggerFactory.getLogger(JPARepositorySaveMethodAspect.class);

    @Around("execution(* com.cobnet.interfaces.spring.repository.JPABaseRepository+.save(..))))")
    public Object handleException(ProceedingJoinPoint joinPoint) throws Throwable {

        try {

            return joinPoint.proceed(joinPoint.getArgs());

        } catch (JpaObjectRetrievalFailureException ex) {

            if(ex.getCause() instanceof EntityNotFoundException cause) {

                String[] words = cause.getMessage().split(" ");

                String className = words[3];
                String value = words[words.length - 1];

                String[] nodes = className.split("\\.");

                Objects.requireNonNull(ProjectBeanHolder.getRedisCacheManager().getCache(nodes[nodes.length - 1] + "s")).evictIfPresent(value);

                LOG.warn("Do not manually delete tables from DB, or clear Redis before start again! Clearing related caching...");

                return joinPoint.proceed(joinPoint.getArgs());

            } else {

                ex.printStackTrace();
            }

            return null;
        }

    }
}
