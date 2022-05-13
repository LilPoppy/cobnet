package com.cobnet.spring.boot.cache.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.keyvalue.core.mapping.KeyValuePersistentEntity;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.RedisData;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.util.ClassUtils;

import java.time.Duration;
import java.util.Locale;

public class RedisKeyValueExtensionTemplate extends RedisKeyValueTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(RedisKeyValueExtensionTemplate.class);
    private final RedisTemplate<String, ?> template;

    private final RedisKeyValueAdapter adapter;

    /**
     * Create new {@link RedisKeyValueTemplate}.
     *
     * @param adapter        must not be {@literal null}.
     * @param mappingContext must not be {@literal null}.
     */
    public RedisKeyValueExtensionTemplate(RedisTemplate<String, ?> template, RedisKeyValueAdapter adapter, RedisMappingContext mappingContext) {
        super(adapter, mappingContext);
        this.template = template;
        this.adapter = adapter;
    }

    public <T, K> T changeKey(T object, K key) {

        RedisData data = this.getRedisData(object);

        long expiration = this.template.getExpire(new String(this.adapter.createKey(data.getKeyspace(), data.getId())));

        K id = (K) this.getIdentity(object);
        try {

            this.adapter.delete(id, data.getKeyspace(), object.getClass());

        } catch (RuntimeException ex) {

            LOG.error(String.format("changeKey() method failed due to delete origin key %s entity.", id));

            return object;
        }

        data.setId(this.getConverter().getConversionService().convert(key, String.class));
        data.getBucket().put(this.getIdentityField(object), this.getConverter().getConversionService().convert(key, byte[].class));
        data.setTimeToLive(expiration);

        return (T) this.getConverter().read(object.getClass(), this.insert(data));
    }

    public <T> boolean expire(T object, Duration duration) {

        final RedisData data = getRedisData(object);

        if(duration == null) {

            return false;
        }

        return execute(new RedisKeyValueCallback<>() {

            @Override
            public Boolean doInRedis(RedisKeyValueAdapter adapter) {

                byte[] key = adapter.createKey(data.getKeyspace(), data.getId());

                return adapter.execute(callback -> callback.pExpire(key, duration.toMillis()));
            }
        });
    }

    private <T> RedisData getRedisData(T object) {

        final RedisData data = object instanceof RedisData ? (RedisData) object : new RedisData();

        if (!(object instanceof RedisData)) {

            this.getConverter().write(object, data);
        }

        if(data.getKeyspace() == null) {

            data.setKeyspace(resolveKeySpace(object.getClass()));
        }

        if (data.getId() == null) {

            data.setId(this.getConverter().getConversionService().convert(this.getIdentity(object), String.class));
        }

        return data;
    }

    private KeyValuePersistentEntity<?, ?> getKeyValuePersistentEntity(Object object) {

        return this.getMappingContext().getRequiredPersistentEntity(ClassUtils.getUserClass(object));
    }

    private String resolveKeySpace(Class<?> type) {
        return this.getMappingContext().getRequiredPersistentEntity(type).getKeySpace();
    }

    private String getIdentityField(Object object) {

        KeyValuePersistentEntity<?, ?> entity = getKeyValuePersistentEntity(object);

        if (!entity.hasIdProperty()) {
            throw new InvalidDataAccessApiUsageException(
                    String.format("Cannot determine id for type %s", ClassUtils.getUserClass(object)));
        }

        return entity.getIdProperty().getName();
    }

    private Object getIdentity(Object object) {

        KeyValuePersistentEntity<?, ?> entity = getKeyValuePersistentEntity(object);

        if (!entity.hasIdProperty()) {
            throw new InvalidDataAccessApiUsageException(
                    String.format("Cannot determine id for type %s", ClassUtils.getUserClass(object)));
        }

        return entity.getIdentifierAccessor(object).getRequiredIdentifier();
    }
}
