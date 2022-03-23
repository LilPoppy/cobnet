package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.User;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

//TODO cache
@Repository
public interface UserRepository extends JPABaseRepository<User, String>, UserDetailsService {

    Optional<User> findByUsernameEqualsIgnoreCase(@Param("username") String username);

    @Override
    @Transactional
    default UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return findByUsernameEqualsIgnoreCase(username).orElse(null);
    }

}
