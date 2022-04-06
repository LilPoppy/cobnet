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

    @AfterThrowing(pointcut = "execution(* org.springframework.data.repository.CrudRepository+.save(..))))", throwing = "ex")
    public Object handleException(JoinPoint joinPoint, EntityNotFoundException ex) throws InvocationTargetException, IllegalAccessException {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        PersistenceUnitUtil util = ProjectBeanHolder.getEntityManager().getEntityManagerFactory().getPersistenceUnitUtil();

        Object key = ProjectBeanHolder.getRedisCacheKeyGenerator().generate(joinPoint.getTarget(), signature.getMethod(), joinPoint.getArgs());

        for(Method method : joinPoint.getTarget().getClass().getMethods()) {

            Cacheable cacheable = method.getAnnotation(Cacheable.class);

            if(cacheable != null) {

                for(String name : cacheable.cacheNames()) {

                    ProjectBeanHolder.getRedisCacheManager().getCache(name).evictIfPresent(key);
                    ProjectBeanHolder.getRedisCacheManager().getCache(name).evictIfPresent(util.getIdentifier(util));
                }
            }
        }

        return signature.getMethod().invoke(joinPoint.getTarget(), joinPoint.getArgs());
    }
}
