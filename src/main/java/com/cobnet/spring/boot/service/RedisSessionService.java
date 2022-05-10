package com.cobnet.spring.boot.service;

import com.cobnet.spring.boot.configuration.SessionConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RedisSessionService {

    public static final String IP_ADDRESS_INDEX_NAME = FindByIndexNameSessionRepository.class.getName()
            .concat(".IP_ADDRESS_INDEX_NAME");

    private SessionConfiguration configuration;

    private RedisTemplate<String, Object> template;

    private String namespace;

    public RedisSessionService(@Autowired SessionConfiguration configuration, @Autowired RedisTemplate<String, Object> template) {
        this.configuration = configuration;
        this.template = template;
        this.namespace = configuration.getNamespace();
    }

    public <T> Long remove(String index, String key, T... values) {

        return RedisSessionService.this.template.boundSetOps(getIndexKey(index, new String(template.getStringSerializer().serialize(key)))).remove(values);
    }

    public <T> Set<T> getIndexMembers(String index, String key) {

        return (Set<T>) RedisSessionService.this.template.boundSetOps(getIndexKey(index, new String(template.getStringSerializer().serialize(key)))).members();
    }

    public <T> Long add(String index, String key, T... values) {

        return RedisSessionService.this.template.boundSetOps(getIndexKey(index, new String(template.getStringSerializer().serialize(key)))).add(values);
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
