package com.storechain.interfaces.spring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.storechain.spring.boot.entity.User;
import com.storechain.spring.boot.entity.UserGrantedAuthority;

public interface UserAuthoritiesRepository extends JpaRepository<User, List<UserGrantedAuthority>> {

}
