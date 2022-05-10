package com.cobnet.spring.boot.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

@Configuration
@ConfigurationProperties("spring.session")
@EnableRedisHttpSession
@Import(RedisHttpSessionConfiguration.class)
public class SessionConfiguration {

    public static final String IP_ADDRESS = "IP_ADDRESS";

    public final static String CONNECTION_TOKEN = "CONNECTION_TOKEN";

    private String namespace;

    private String storeType;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    @Bean
    @Primary
    public RedisIndexedSessionRepository redisIndexedSessionRepository(RedisIndexedSessionRepository repository) {

        repository.setRedisKeyNamespace(this.getNamespace());

        return repository;
    }

}
