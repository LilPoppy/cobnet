package com.cobnet.interfaces.spring.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.Duration;

@NoRepositoryBean
public interface RedisRepository<T, ID> extends CrudRepository<T, ID> {

    public boolean expire(T entity, Duration duration);

    public T key(T entity, ID key);

    public T save(T entity, Duration expiration);
}
