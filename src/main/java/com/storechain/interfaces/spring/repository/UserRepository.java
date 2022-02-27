package com.storechain.interfaces.spring.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.storechain.spring.boot.entity.User;

@Repository
public interface UserRepository extends ExtensionRepository<User, String>, UserDetailsService {

    @Transactional
    @Modifying
	default UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		return findByUsernameIgnoreCase(username);
	}
    
    User findByUsernameIgnoreCase(@Param("username") String username);
	
}
