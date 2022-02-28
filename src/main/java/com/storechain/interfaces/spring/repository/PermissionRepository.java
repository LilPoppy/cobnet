package com.storechain.interfaces.spring.repository;

import org.springframework.stereotype.Repository;

import com.storechain.spring.boot.entity.UserPermission;

@Repository
public interface PermissionRepository extends ExtensionRepository<UserPermission, String>  {

}
