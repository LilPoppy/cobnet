package com.storechain.interfaces.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.storechain.spring.boot.entity.UserGrantedAuthority;

public interface UserGrantedAuthorityRepository extends JpaRepository<UserGrantedAuthority, String> {

}
