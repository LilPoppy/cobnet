package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.UserRole;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Cacheable("UserRoles")
public interface UserRoleRepository extends JPABaseRepository<UserRole, String> {

    Optional<UserRole> findByRoleEqualsIgnoreCase(String role);
}
