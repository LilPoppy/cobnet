package com.storechain.utils;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.google.common.reflect.TypeToken;
import com.storechain.interfaces.spring.service.RedisService;

public class DatabaseManager {
	
	@SuppressWarnings({ "unchecked", "serial" })
	public static <K, V> RedisService<K, V> getRedisService(Class<K> key, Class<V> value) {
		
		return (RedisService<K, V>) SpringManager.getContext().getAutowireCapableBeanFactory().getBean(new TypeToken<RedisService<K, V>>() {}.getRawType());
	}

	@SuppressWarnings({ "unchecked", "serial" })
	public static <T> RedisService<String, T> getRedisService(Class<T> value) {
		
		return (RedisService<String, T>) SpringManager.getContext().getAutowireCapableBeanFactory().getBean(new TypeToken<RedisService<String, T>>() {}.getRawType());
	}
	
	@SuppressWarnings({ "unchecked", "serial" })
	public static <K, V> RedisTemplate<K, V> getRedisTemplate(Class<K> key, Class<V> value) {
		
		return (RedisTemplate<K, V>) SpringManager.getContext().getAutowireCapableBeanFactory().getBean(new TypeToken<RedisTemplate<K,V>>() {}.getRawType());
	}
	
	public static <T, ID> CrudRepository<T, ID> getCrudRepository(Class<? extends CrudRepository<T, ID>> repo) {
		
		return SpringManager.getContext().getAutowireCapableBeanFactory().getBean(repo);
	}
	
	@SuppressWarnings({ "serial", "unchecked" })
	public static <T, ID> CrudRepository<T, ID> getCrudRepository(Class<T> entity, Class<ID> primaryKey) {
		
		return (CrudRepository<T, ID>) SpringManager.getContext().getAutowireCapableBeanFactory().getBean(new TypeToken<CrudRepository<T, ID>>() {}.getRawType());
	}
	
	public static <T, ID> PagingAndSortingRepository<T, ID> getPagingAndSortingRepository(Class<? extends PagingAndSortingRepository<T, ID>> repo) {
		
		return SpringManager.getContext().getAutowireCapableBeanFactory().getBean(repo);
	}
	
	@SuppressWarnings({ "serial", "unchecked" })
	public static <T, ID> PagingAndSortingRepository<T, ID> getPagingAndSortingRepository(Class<T> entity, Class<ID> primaryKey) {
		
		return (PagingAndSortingRepository<T, ID>) SpringManager.getContext().getAutowireCapableBeanFactory().getBean(new TypeToken<PagingAndSortingRepository<T, ID>>() {}.getRawType());
	}

	public static <T, ID> JpaRepository<T, ID> getJpaRepository(Class<? extends JpaRepository<T, ID>> repo) {
	    
	    return SpringManager.getContext().getAutowireCapableBeanFactory().getBean(repo);
	}
	
	@SuppressWarnings({ "serial", "unchecked" })
	public static <T, ID> JpaRepository<T, ID> getJpaRepository(Class<T> entity, Class<ID> primaryKey) {
		
		return (JpaRepository<T, ID>) SpringManager.getContext().getAutowireCapableBeanFactory().getBean(new TypeToken<JpaRepository<T, ID>>() {}.getRawType());
	}
	
}
