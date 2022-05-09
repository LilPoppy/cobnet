package com.cobnet.spring.boot.service;

import com.cobnet.common.DateUtils;
import com.cobnet.event.account.AccountLoginEvent;
import com.cobnet.event.account.AccountPasswordEncodingEvent;
import com.cobnet.exception.AuthenticationCancelledException;
import com.cobnet.exception.AuthenticationSecurityException;
import com.cobnet.exception.ResponseFailureStatusException;
import com.cobnet.interfaces.security.Account;
import com.cobnet.interfaces.spring.repository.UserRepository;
import com.cobnet.security.AccountAuthenticationToken;
import com.cobnet.spring.boot.configuration.SecurityConfiguration;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.cobnet.spring.boot.dto.support.*;
import com.cobnet.spring.boot.entity.Address;
import com.cobnet.spring.boot.entity.User;
import com.cobnet.spring.boot.service.support.AccountPhoneNumberVerifyCache;
import com.cobnet.spring.boot.service.support.AttemptLoginCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
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
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {

    private static Logger LOG = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder encoder;

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if(!authentication.isAuthenticated()) {

            HttpServletRequest request = ProjectBeanHolder.getCurrentHttpRequest();

            assert request != null;

            HttpSession session = request.getSession(true);

            if(ProjectBeanHolder.getSecurityConfiguration().getHumanValidation().isEnable() && !ProjectBeanHolder.getHumanValidator().isValidated(session.getId())) {

                throw new AuthenticationSecurityException(AuthenticationStatus.HUMAN_VALIDATION_REQUIRED);
            }

            String key = request.getSession(true).getId();

            //ProjectBeanHolder.getCacheService().evict(HumanValidationCache.HumanValidatorKey, key);

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

                    if(account instanceof User user) {

                        AttemptLoginCache cache = ProjectBeanHolder.getCacheService().get(AttemptLoginCache.AccountServiceName, AttemptLoginCache.class, key);

                        if(cache == null || !DateUtils.addDuration(cache.creationTime(), ProjectBeanHolder.getSecurityConfiguration().getMaxAttemptLoginReset()).after(DateUtils.now())) {

                            cache = new AttemptLoginCache(DateUtils.now(), 0);

                            ProjectBeanHolder.getCacheService().set(AttemptLoginCache.AccountServiceName, key, cache, ProjectBeanHolder.getSecurityConfiguration().getMaxAttemptLoginReset());
                        }

                        if(cache.count() >= ProjectBeanHolder.getSecurityConfiguration().getMaxAttemptLogin()) {

                            throw new AuthenticationSecurityException(AuthenticationStatus.REACHED_MAXIMUM_ATTEMPT, "Authenticate reached maximum tries.");
                        }

                        if(user.getPassword().equals(authentication.getCredentials().toString()) || (!user.isPasswordEncoded() && encoder.matches(user.getPassword(), authentication.getCredentials().toString()))) {

                            cache = new AttemptLoginCache(DateUtils.now(), 0);

                            ProjectBeanHolder.getCacheService().set(AttemptLoginCache.AccountServiceName, key, cache, ProjectBeanHolder.getSecurityConfiguration().getMaxAttemptLoginReset());

                            if(!user.isPasswordEncoded()) {

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

                        cache = new AttemptLoginCache(cache.creationTime(), cache.count() + 1);

                        ProjectBeanHolder.getCacheService().set(AttemptLoginCache.AccountServiceName, key, cache, ProjectBeanHolder.getSecurityConfiguration().getMaxAttemptLoginReset().minus(DateUtils.getInterval(DateUtils.now(), cache.creationTime())));

                        if(ProjectBeanHolder.getSecurityConfiguration().isMaxAttemptLoginAccountLocks() && cache.count() >= ProjectBeanHolder.getSecurityConfiguration().getMaxAttemptLogin()) {

                            user.setLocked(true);
                            user.setLockTime(DateUtils.addDuration(DateUtils.now(), ProjectBeanHolder.getSecurityConfiguration().getMaxAttemptLoginAccountLocksDuration()));
                            ProjectBeanHolder.getUserRepository().save(user);
                        }

                        throw new BadCredentialsException("Wrong password");
                    }
                }

                throw new AuthenticationCancelledException("Authentication cancelled.");
            }

            throw new UsernameNotFoundException("User " + username + " is not exist!");
        }

        return authentication;
    }

    public User register(UserRegisterForm form) throws ResponseFailureStatusException {

        if(ProjectBeanHolder.getSecurityConfiguration().isPhoneNumberVerifyEnable()) {

            AccountPhoneNumberVerifyCache cache = ProjectBeanHolder.getPhoneNumberSmsVerifyService().getCache(form.getUsername());

            if (cache != null) {

                try {

                    return register(form.getEntity(), form.getAddress().getEntity());

                } finally {

                    if (cache.type() == PhoneNumberSmsType.ACCOUNT_REGISTER && cache.verified())
                    {
                        ProjectBeanHolder.getPhoneNumberSmsVerifyService().delete(form.getUsername());
                    }

                    throw new ResponseFailureStatusException(UserRegisterResultStatus.VERIFICATION_FAILED);
                }
            }

            throw new ResponseFailureStatusException(UserRegisterResultStatus.REJECTED, new CommentWrapper("SMS request required.", new MethodHint(HttpMethod.POST, "/visitor/sms/request", new PhoneNumberSmsRequest(form.getUsername(), form.getPhoneNumber(), PhoneNumberSmsType.ACCOUNT_REGISTER))));
        }

        return register(form.getEntity(), form.getAddress().getEntity());
    }

    public User register(User user, Address... addresses) throws ResponseFailureStatusException {

        if(user == null) {

            throw new ResponseFailureStatusException(UserRegisterResultStatus.UNACCEPTABLE_CONTENT);
        }

        user.getAddresses().addAll(List.of(addresses));

        UserRepository repository = ProjectBeanHolder.getUserRepository();

        SecurityConfiguration configuration = ProjectBeanHolder.getSecurityConfiguration();

        List<UserRegisterFormFieldType> fields = new ArrayList<>();

        if(!user.getUsername().matches(configuration.getUsernameFormatRegex()) || repository.existsById(user.getUsername())) {

            fields.add(UserRegisterFormFieldType.USERNAME);
        }

        if(!user.getPassword().matches(configuration.getPasswordFormatRegex())) {

            fields.add(UserRegisterFormFieldType.PASSWORD);
        }

        String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

        if(!user.getEmail().matches(emailRegex) || repository.countByEmailEquals(user.getEmail()) >= configuration.getEmailMaxUse()) {

            fields.add(UserRegisterFormFieldType.EMAIL);
        }

        if(user.getPhoneNumber() == null || repository.countByPhoneNumberContaining(user.getPhoneNumber()) >= configuration.getPhoneNumberMaxUse()) {

            fields.add(UserRegisterFormFieldType.PHONE_NUMBER);
        }

        if(user.getFirstName() == null || user.getFirstName().length() < 1) {

            fields.add(UserRegisterFormFieldType.FIRST_NAME);
        }

        if(user.getLastName() == null || user.getLastName().length() < 1) {

            fields.add(UserRegisterFormFieldType.LAST_NAME);
        }

        if(fields.size() > 0) {

            throw new ResponseFailureStatusException(UserRegisterResultStatus.UNACCEPTABLE_CONTENT, new ObjectWrapper<>("fields", fields));
        }

        repository.save(user);

        ProjectBeanHolder.getCacheService().evictIfPresent(AccountPhoneNumberVerifyCache.PhoneNumberSmsVerifyServiceKey, AccountPhoneNumberVerifyCache.class, user.getUsername());

        return user;
    }

}
