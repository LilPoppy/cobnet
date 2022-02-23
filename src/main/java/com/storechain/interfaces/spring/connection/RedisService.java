package com.storechain.interfaces.spring.connection;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public interface RedisService<V> {
	
	public boolean set(String key, V value);
	
	public V get(String key);
	
	public boolean expire(String key, long expire, TimeUnit unit);
	
	public boolean expire(String key, Date date);
	
	boolean remove(String key);
	
    boolean exists(final String key);
}
