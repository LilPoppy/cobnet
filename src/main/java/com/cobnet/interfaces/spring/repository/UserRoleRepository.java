package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.UserRole;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaExtensionRepository<UserRole, String> {

    UserRole findByRoleEqualsIgnoreCase(String role);
}
