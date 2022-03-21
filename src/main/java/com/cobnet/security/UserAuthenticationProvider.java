package com.cobnet.security;

import com.cobnet.interfaces.security.Account;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import java.util.Arrays;

public class UserAuthenticationProvider implements AuthenticationProvider {

    private final static Logger LOG = LoggerFactory.getLogger(UserAuthenticationProvider.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        Object principal = authentication.getPrincipal();

        String username = null;

        if(principal instanceof String) {

            username = (String) principal;
        }

        Assert.notNull(username, "Username cannot be null.");

        UserDetails details = userDetailsService.loadUserByUsername(username);

        if(details instanceof Account account) {

            boolean match = details.getPassword().equals(authentication.getCredentials().toString());
            boolean isEncodedSourceMatchTarget = encoder.matches(authentication.getCredentials().toString(), details.getPassword());
            boolean isEncodedTargetMatchSource = encoder.matches(details.getPassword(), authentication.getCredentials().toString());

            if(match || isEncodedSourceMatchTarget || isEncodedTargetMatchSource) {

                if (!isEncodedSourceMatchTarget && (match || isEncodedTargetMatchSource) && isRaw(details.getPassword())) {

                    if(account instanceof User user) {

                        LOG.debug("Encoding password for user: " + user.getUsername());

                        user.setPassword(encoder.encode(user.getPassword()));

                        ProjectBeanHolder.getUserRepository().save(user);
                    }
                }

                return new UserAuthenticationToken(account, authentication.getCredentials());
            }

            throw new BadCredentialsException("Wrong password");
        }

        throw new UsernameNotFoundException("User " + username + " is not exist!");
    }

    private boolean isRaw(String password) {

        try {

            encoder.upgradeEncoding(password);

        } catch (IllegalArgumentException ex) {

            return true;
        }

        return false;
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return Arrays.stream(new Class<?>[]{ UsernamePasswordAuthenticationToken.class}).anyMatch(clazz -> clazz.isAssignableFrom(authentication));
    }
}