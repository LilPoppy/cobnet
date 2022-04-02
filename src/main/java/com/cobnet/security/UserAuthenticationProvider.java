package com.cobnet.security;

import com.cobnet.event.account.AccountLoginEvent;
import com.cobnet.event.account.AccountPasswordEncodingEvent;
import com.cobnet.exception.AuthenticationCancelledException;
import com.cobnet.interfaces.security.Account;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Objects;
import java.util.stream.Stream;

public class UserAuthenticationProvider implements AuthenticationProvider {

    private final static Logger LOG = LoggerFactory.getLogger(UserAuthenticationProvider.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if(!authentication.isAuthenticated()) {

            Object principal = authentication.getPrincipal();

            String username = null;

            if (principal instanceof String) {

                username = (String) principal;
            }

            Assert.notNull(username, "Username cannot be null.");

            UserDetails details = userDetailsService.loadUserByUsername(username);

            if (details instanceof Account account) {

                AccountLoginEvent loginEvent = new AccountLoginEvent(account);

                ProjectBeanHolder.getApplicationEventPublisher().publishEvent(loginEvent);

                if(details.getUsername() == null && details.getPassword() == null) {

                    throw new UsernameNotFoundException("User " + username + " is not exist!");
                }

                if (!loginEvent.isCancelled()) {

                    if (details.getPassword().equals(authentication.getCredentials().toString()) || encoder.matches(authentication.getCredentials().toString(), details.getPassword()) || encoder.matches(details.getPassword(), authentication.getCredentials().toString())) {

                        if (account instanceof User user && !user.isPasswordEncoded()) {

                            AccountPasswordEncodingEvent encodingEvent = new AccountPasswordEncodingEvent(account, user.getPassword(), encoder);

                            ProjectBeanHolder.getApplicationEventPublisher().publishEvent(encodingEvent);

                            if (!encodingEvent.isCancelled()) {

                                LOG.debug("Encoding password for user: " + user.getUsername());

                                user.setPassword(encoder.encode(encodingEvent.getPassword()));
                                user.setPasswordEncoded(true);

                                ProjectBeanHolder.getUserRepository().save(user);
                            }
                        }


                        return new AccountAuthenticationToken(account, ((UserDetails) account).getPassword());
                    }

                    throw new BadCredentialsException("Wrong password");
                }

                throw new AuthenticationCancelledException("Authentication cancelled.");
            }

            throw new UsernameNotFoundException("User " + username + " is not exist!");
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return Stream.of(UsernamePasswordAuthenticationToken.class).anyMatch(clazz -> clazz.isAssignableFrom(authentication));
    }
}