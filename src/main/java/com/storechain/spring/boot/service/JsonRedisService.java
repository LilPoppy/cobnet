package com.storechain.spring.boot.service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import com.storechain.interfaces.spring.connection.RedisService;

@Service("RedisService")
public class JsonRedisService<V> implements RedisService<V> {

    @Resource
    private RedisTemplate<String, V> redisTemplate;
    
	@Override
	public boolean set(String key, V value) {
		
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
        	
			@Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
            	
                connection.set(redisTemplate.getStringSerializer().serialize(key), ((Jackson2JsonRedisSerializer<?>) redisTemplate.getValueSerializer()).serialize(value));
                
                return true;
            }
        });
        
        return result;
	}

	@Override
	public V get(String key) {
		
        V result = redisTemplate.execute(new RedisCallback<V>() {
        	
            @SuppressWarnings("unchecked")
			@Override
            public V doInRedis(RedisConnection connection) throws DataAccessException {
            	
				byte[] value = connection.get(redisTemplate.getStringSerializer().serialize(key));
                
                return (V) redisTemplate.getValueSerializer().deserialize(value);
            }
        });
        
        return result;
	}

	@Override
	public boolean expire(String key, long expire, TimeUnit unit) {
		
		return redisTemplate.expire(key, expire, unit);
	}

	@Override
	public boolean expire(String key, Date date) {

		return this.expire(key, date.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}

	@Override
	public boolean remove(String key) {
		
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
        	
			@Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
            	
                connection.del(redisTemplate.getStringSerializer().serialize(key));
                
                return true;
            }
        });
        
        return result;
	}
}
