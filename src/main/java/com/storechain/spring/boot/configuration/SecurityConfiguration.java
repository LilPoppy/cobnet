package com.storechain.spring.boot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();    
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http.formLogin()
        .loginPage("/login")
        .and()
        .authorizeRequests()
        .antMatchers("/register", "/doRegister", "/login", "/doLogin").permitAll()
        .anyRequest().authenticated()
        .and()
        .csrf().disable();
    }
}
