package com.storechain.interfaces.spring.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.storechain.spring.boot.entity.User;

@Primary
public interface UserRepository extends JpaRepository<User, String>, UserDetailsService {

	default UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		return this.findById(username).orElse(null);
	}
	
}
