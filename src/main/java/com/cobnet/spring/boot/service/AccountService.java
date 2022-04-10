package com.cobnet.spring.boot.service;

import com.cobnet.event.account.AccountLoginEvent;
import com.cobnet.event.account.AccountPasswordEncodingEvent;
import com.cobnet.exception.AuthenticationCancelledException;
import com.cobnet.exception.HumanValidationFailureException;
import com.cobnet.interfaces.security.Account;
import com.cobnet.security.AccountAuthenticationToken;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;

@Service
public class AccountService {

    private static Logger LOG = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder encoder;

    public Authentication authenticate (Authentication authentication) throws AuthenticationException {

        if(!authentication.isAuthenticated()) {

            HttpServletRequest request = ProjectBeanHolder.getCurrentHttpRequest();

            assert request != null;

            Cache.ValueWrapper cache = ProjectBeanHolder.getRedisCacheManager().getCache(HumanValidator.class.getSimpleName()).get(request.getSession(true).getId() + "::" + HumanValidator.VALIDATED_KEY);

            if((!ProjectBeanHolder.getSecurityConfiguration().isHumanValidationEnable()) || (cache != null && cache.get() instanceof Boolean validated && validated)) {

                System.out.println("authen@@@");
                request.getSession().setAttribute(HumanValidator.VALIDATED_KEY, false);

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

                    if (details.getUsername() == null && details.getPassword() == null) {

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

            throw new HumanValidationFailureException("Human validation failure!");
        }

        return authentication;
    }
}
