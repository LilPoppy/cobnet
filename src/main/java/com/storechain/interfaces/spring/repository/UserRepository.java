package com.storechain.interfaces.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.storechain.spring.boot.entity.User;

public interface UserRepository extends JpaRepository<User, String> {

}
