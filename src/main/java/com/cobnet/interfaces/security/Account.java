package com.cobnet.interfaces.security;

import com.cobnet.spring.boot.core.ProjectBeanHolder;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface Account {

    public String getUsername();

    public Collection<? extends GrantedAuthority> getAuthorities();

    public static Account getCurrentAccount() {

        return ProjectBeanHolder.getCurrentAccount();
    }
}
