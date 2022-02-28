package com.storechain.interfaces.spring.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.storechain.spring.boot.entity.User;
import com.storechain.spring.boot.entity.UserRole;

@Repository
public interface UserRoleRepository extends ExtensionRepository<UserRole, String> {
	
	UserRole findByRoleIgnoreCase(@Param("role") String role);
}
