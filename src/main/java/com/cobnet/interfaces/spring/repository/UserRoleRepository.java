package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.User;
import com.cobnet.spring.boot.entity.UserRole;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JPABaseRepository<UserRole, String> {

    @Cacheable(value = "UserRoles", unless="#result == null")
    Optional<UserRole> findByRoleEqualsIgnoreCase(String role);

    @CacheEvict(cacheNames = "UserRoles", key = "#entity.getName()")
    @Override
    void delete(UserRole entity);
}
