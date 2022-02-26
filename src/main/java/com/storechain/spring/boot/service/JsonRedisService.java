package com.storechain.spring.boot.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import com.storechain.interfaces.spring.service.RedisService;

@Service
public class JsonRedisService<K,V> implements RedisService<K, V> {

    @Resource
    private RedisTemplate<K, V> redisTemplate;
    
	@Override
	public boolean set(K key, V value, Expiration expiration, SetOption option) {
		
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
        	
			@Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				
				@SuppressWarnings("unchecked")
				byte[] keyBuf = (key instanceof String ? (RedisSerializer<K>) redisTemplate.getStringSerializer() : (Jackson2JsonRedisSerializer<K>) redisTemplate.getHashKeySerializer()).serialize(key);
				@SuppressWarnings("unchecked")
				byte[] valueBuf = ((Jackson2JsonRedisSerializer<V>) redisTemplate.getValueSerializer()).serialize(value);
            	
				if(expiration != null && option != null) {
					
					connection.set(keyBuf, valueBuf, expiration, option);
				} else {
					
	                connection.set(keyBuf, valueBuf);
				}
				
                return true;
            }
        });
        
        return result;
	}

	@Override
	public V get(K key) {
		
        V result = redisTemplate.execute(new RedisCallback<V>() {
        	
            @SuppressWarnings("unchecked")
			@Override
            public V doInRedis(RedisConnection connection) throws DataAccessException {
            	
				byte[] value = connection.get((key instanceof String ? (RedisSerializer<K>) redisTemplate.getStringSerializer() : (Jackson2JsonRedisSerializer<K>) redisTemplate.getHashKeySerializer()).serialize(key));
                
                return (V) redisTemplate.getValueSerializer().deserialize(value);
            }
        });
        
        return result;
	}

	@Override
	public boolean expire(K key, long expire, TimeUnit unit) {
		
		return redisTemplate.execute(new RedisCallback<Boolean>() {

			@SuppressWarnings("unchecked")
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				
				System.out.println(unit + ":" + expire + "= Seconds:" + TimeUnit.SECONDS.convert(expire, unit));
				return connection.expire((key instanceof String ? (RedisSerializer<K>) redisTemplate.getStringSerializer() : (Jackson2JsonRedisSerializer<K>) redisTemplate.getHashKeySerializer()).serialize(key), TimeUnit.SECONDS.convert(expire, unit));
			}
			
		});
	}

	@Override
	public boolean remove(K key) {
		
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
        	
			@SuppressWarnings("unchecked")
			@Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
            	
                connection.del((key instanceof String ? (RedisSerializer<K>) redisTemplate.getStringSerializer() : (Jackson2JsonRedisSerializer<K>) redisTemplate.getHashKeySerializer()).serialize(key));
                
                return true;
            }
        });
        
        return result;
	}

	@Override
	public boolean exists(K key) {
		
		return redisTemplate.execute(new RedisCallback<Boolean>() {

			@SuppressWarnings("unchecked")
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {

				return connection.exists((key instanceof String ? (RedisSerializer<K>) redisTemplate.getStringSerializer() : (Jackson2JsonRedisSerializer<K>) redisTemplate.getHashKeySerializer()).serialize(key));
			}
			
		});
	}
}
