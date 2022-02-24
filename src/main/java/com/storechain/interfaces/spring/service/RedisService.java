package com.storechain.interfaces.spring.service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.core.types.Expiration;

public interface RedisService<K, V> {
	
	default boolean set(K key, V value) {
		
		return this.set(key, value, null, null);
	}
	
	public boolean set(K key, V value, Expiration expiration, SetOption option);
	
	public V get(K key);
	
	public boolean expire(K key, long expire, TimeUnit unit);
	
	default boolean expire(K key, Date date) {
		
		return this.expire(key, date.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}
	
	boolean remove(K key);
	
    boolean exists(final K key);
}
