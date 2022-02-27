package com.storechain.interfaces.spring.repository;

import org.springframework.stereotype.Repository;

import com.storechain.spring.boot.entity.UserGrantedAuthority;

@Repository
public interface UserGrantedAuthorityRepository extends ExtensionRepository<UserGrantedAuthority, Integer> {

}
