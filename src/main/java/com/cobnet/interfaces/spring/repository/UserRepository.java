package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface UserRepository extends JPABaseRepository<User, String>, UserDetailsService {

    @Transactional
    Optional<User> findByUsernameEqualsIgnoreCase(@Param("username") String username);

    @Override
    @Cacheable("Users")
    default UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return findByUsernameEqualsIgnoreCase(username).orElse(new User());
    }

    @CacheEvict(cacheNames = "Users", key = "#entity.getUsername()")
    @Override
    void delete(User entity);

    @CacheEvict(cacheNames = "Users", key = "#entity.getUsername()")
    @Override
    <S extends User> S save(S entity);

    long countByPhoneNumberContaining(String phoneNumber);

    long countByEmailEquals(String email);
}
