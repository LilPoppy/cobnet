package com.cobnet.spring.boot.cache.support;

import org.springframework.data.convert.CustomConversions;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.convert.RedisConverter;
import org.springframework.data.redis.core.mapping.RedisMappingContext;

public class RedisKeyValueExtensionAdapter extends RedisKeyValueAdapter {

    public RedisKeyValueExtensionAdapter(RedisOperations<?, ?> redisOps) {
        super(redisOps);
    }

    public RedisKeyValueExtensionAdapter(RedisOperations<?, ?> redisOps, RedisMappingContext mappingContext) {
        super(redisOps, mappingContext);
    }

    public RedisKeyValueExtensionAdapter(RedisOperations<?, ?> redisOps, RedisMappingContext mappingContext, CustomConversions customConversions) {
        super(redisOps, mappingContext, customConversions);
    }

    public RedisKeyValueExtensionAdapter(RedisOperations<?, ?> redisOps, RedisConverter redisConverter) {

        super(redisOps, redisConverter);
    }
}
