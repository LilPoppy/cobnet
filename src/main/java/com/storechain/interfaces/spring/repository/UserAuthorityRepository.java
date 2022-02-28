package com.storechain.interfaces.spring.repository;

import org.springframework.stereotype.Repository;

import com.storechain.spring.boot.entity.UserAuthority;

@Repository
public interface UserAuthorityRepository extends ExtensionRepository<UserAuthority, String> {

}
