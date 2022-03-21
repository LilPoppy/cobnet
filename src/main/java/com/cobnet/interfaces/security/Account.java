package com.cobnet.interfaces.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public interface Account {

    public String getUsername();

    public Collection<? extends GrantedAuthority> getAuthorities();
}
