package com.cobnet.spring.boot.cache.support;

import org.springframework.data.redis.core.convert.Bucket;
import org.springframework.data.redis.core.convert.DefaultRedisTypeMapper;
import org.springframework.data.redis.core.convert.RedisTypeMapper;
import org.springframework.data.util.TypeInformation;

import java.util.Locale;
import java.util.Map;

public class RedisCustomTypeMapper extends DefaultRedisTypeMapper {

    @Override
    public boolean isTypeKey(String key) {

        return super.isTypeKey(key);
    }

    @Override
    public TypeInformation<?> readType(Bucket.BucketPropertyPath source) {

        return super.readType(source);
    }

    @Override
    public <T> TypeInformation<? extends T> readType(Bucket.BucketPropertyPath source, TypeInformation<T> defaultType) {

        return super.readType(source, defaultType);
    }

    @Override
    public void writeType(Class<?> type, Bucket.BucketPropertyPath dbObject) {

        super.writeType(type, dbObject);
    }

    @Override
    public void writeType(TypeInformation<?> type, Bucket.BucketPropertyPath dbObject) {

        super.writeType(type, dbObject);
    }
}
