package com.cobnet.spring.boot.cache.support;

import com.cobnet.interfaces.spring.repository.RedisRepository;
import org.springframework.data.keyvalue.core.KeyValueOperations;
import org.springframework.data.keyvalue.repository.support.SimpleKeyValueRepository;
import org.springframework.data.repository.core.EntityInformation;

import java.time.Duration;

public class RedisKeyValueExtensionRepository<T, ID> extends SimpleKeyValueRepository<T, ID> implements RedisRepository<T, ID> {

    private EntityInformation metadata;

    private KeyValueOperations operations;

    /**
     * Creates a new {@link SimpleKeyValueRepository} for the given {@link EntityInformation} and
     * {@link KeyValueOperations}.
     *
     * @param metadata   must not be {@literal null}.
     * @param operations must not be {@literal null}.
     */
    public RedisKeyValueExtensionRepository(EntityInformation<T, ID> metadata, KeyValueOperations operations) {
        super(metadata, operations);
        this.metadata = metadata;
        this.operations = operations;

    }

    @Override
    public boolean expire(T entity, Duration duration) {

        return ((RedisKeyValueExtensionTemplate)this.operations).expire(entity, duration);
    }

    @Override
    public T key(T entity, ID key) {

        return ((RedisKeyValueExtensionTemplate)this.operations).changeKey(entity, key);
    }

    @Override
    public T save(T entity, Duration expiration) {

        T result = super.save(entity);

        if(expire(entity, expiration)) {

            return result;
        }

        return null;
    }


}
