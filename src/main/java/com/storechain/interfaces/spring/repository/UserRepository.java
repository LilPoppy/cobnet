package com.storechain.interfaces.spring.repository;

import javax.transaction.Transactional;

import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.storechain.spring.boot.entity.User;
import com.storechain.spring.boot.entity.UserProvider;

@Repository
public interface UserRepository extends ExtensionRepository<User, String>, UserDetailsService {

    @Transactional
	default UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		return findByUsernameIgnoreCase(username);
	}
    
    User findByUsernameIgnoreCase(@Param("username") String username);
    
    User findFirstByProviders(UserProvider provider);
	
}
