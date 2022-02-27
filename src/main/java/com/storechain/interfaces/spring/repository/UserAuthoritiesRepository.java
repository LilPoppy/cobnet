package com.storechain.interfaces.spring.repository;

import org.springframework.stereotype.Repository;

import com.storechain.spring.boot.entity.UserGrantedAuthorities;

@Repository
public interface UserAuthoritiesRepository extends ExtensionRepository<UserGrantedAuthorities, Long> {

}
