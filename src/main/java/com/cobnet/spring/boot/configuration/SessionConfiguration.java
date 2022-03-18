package com.cobnet.spring.boot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * TODO Unable to pass test in build
 * Caused by: org.springframework.beans.factory.BeanCreationException:
 * Error creating bean with name 'org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration': Initialization of bean failed;
 * nested exception is java.lang.NullPointerException: Cannot invoke "org.springframework.util.StringValueResolver.resolveStringValue(String)" because "this.embeddedValueResolver" is null
 */
//@EnableRedisHttpSession

@Configuration
@ConfigurationProperties("spring.session")
public class SessionConfiguration {

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

}
