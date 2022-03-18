package com.cobnet.interfaces.spring.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository {//extends JpaExtensionRepository {

//    @Transactional
//    default UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//
//        return findByUsernameIgnoreCase(username);
//    }

//    User findByUsernameIgnoreCase(@Param("username") String username);

}
