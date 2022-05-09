package com.cobnet.spring.boot.service;

import com.cobnet.spring.boot.configuration.RedisConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RedisSessionService {

    private RedisConfiguration configuration;

    private RedisTemplate<String, Object> template;

    private String namespace;

    public RedisSessionService(@Autowired RedisConfiguration configuration, @Autowired RedisTemplate<String, Object> template) {

        this.configuration = configuration;
        this.template = template;
        this.namespace = configuration.getNamespace();
    }


    public Set<String> getIndexKeys(String index) {

        final String prefix = getIndex(index);

        return Objects.requireNonNull(template.keys(new StringBuilder(prefix).append("*").toString()).stream().map(key -> key.substring(prefix.length())).collect(Collectors.toSet()));
    }

    String getIndex(String index) {

        return new StringBuilder(this.namespace).append(":index:").append(index).append(":").toString();
    }

    String getIndexKey(String indexKey, String indexValue) {

        return new StringBuilder(getIndex(indexKey)).append(indexValue).toString();
    }
}
