package com.cobnet.spring.boot.service;

import com.cobnet.common.DateUtils;
import com.cobnet.event.account.AccountLoginEvent;
import com.cobnet.event.account.AccountPasswordEncodingEvent;
import com.cobnet.exception.AuthenticationCancelledException;
import com.cobnet.exception.HumanValidationFailureException;
import com.cobnet.interfaces.security.Account;
import com.cobnet.interfaces.spring.repository.UserRepository;
import com.cobnet.security.AccountAuthenticationToken;
import com.cobnet.spring.boot.configuration.SecurityConfiguration;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.cobnet.spring.boot.dto.support.*;
import com.cobnet.spring.boot.entity.User;
import com.cobnet.spring.boot.service.support.AccountPhoneNumberVerifyCache;
import com.cobnet.spring.boot.service.support.HumanValidationCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

            String key = request.getSession(true).getId();

            HumanValidationCache cache = ProjectBeanHolder.getHumanValidator().getCache(key);

            if(!ProjectBeanHolder.getSecurityConfiguration().isHumanValidationEnable() || (cache != null && cache.isValidated())) {

                ProjectBeanHolder.getCacheService().evict(HumanValidationCache.HumanValidatorKey, key);

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

                            if(user.getPassword().equals(authentication.getCredentials().toString()) || (!user.isPasswordEncoded() && encoder.matches(user.getPassword(), authentication.getCredentials().toString()))) {

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

                            throw new BadCredentialsException("Wrong password");
                        }
                    }

                    throw new AuthenticationCancelledException("Authentication cancelled.");
                }

                throw new UsernameNotFoundException("User " + username + " is not exist!");
            }

            throw new HumanValidationFailureException("Human validation failure!");
        }

        return authentication;
    }

    public PhoneNumberSmsRequestResult requestPhoneNumberSms(PhoneNumberSmsRequest request) {

        if(request.type() == PhoneNumberSmsType.ACCOUNT_REGISTER) {

            long count = ProjectBeanHolder.getUserRepository().countByPhoneNumberContaining(request.phoneNumber());

            if(count > ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberMaxUse()) {

                return new PhoneNumberSmsRequestResult(PhoneNumberSmsRequestResultStatus.NUMBER_OVERUSED);
            }
        }

        AccountPhoneNumberVerifyCache cache = ProjectBeanHolder.getCacheService().get(AccountPhoneNumberVerifyCache.AccountServiceKey, request.username(), AccountPhoneNumberVerifyCache.class);

        if(cache != null) {

            if (DateUtils.addDuration(cache.createdTime(), ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsGenerateInterval()).before(DateUtils.now())) {

                return sendSms(request);
            }

            return new PhoneNumberSmsRequestResult(PhoneNumberSmsRequestResultStatus.INTERVAL_LIMITED);
        }

        return sendSms(request);
    }

    private PhoneNumberSmsRequestResult sendSms(PhoneNumberSmsRequest request) {

        Random random = new Random();

        int code = random.nextInt(987654 + 1 - 123456) + 123456;

        ProjectBeanHolder.getCacheService().set(AccountPhoneNumberVerifyCache.AccountServiceKey, request.username(), new AccountPhoneNumberVerifyCache(code, DateUtils.now(), request.type(), false), ProjectBeanHolder.getSecurityConfiguration().getHumanValidationExpire());

        ProjectBeanHolder.getMessager().message(request.phoneNumber(), ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberVerifySmsMessage().replace("$(code)", Integer.toString(code))).create();

        return new PhoneNumberSmsRequestResult(PhoneNumberSmsRequestResultStatus.SUCCESS);
    }

    public PhoneNumberSmsVerifyResult requestPhoneNumberVerify(PhoneNumberSmsVerify verify) {

        if(getSmsCode(verify.username(), verify.type()) == verify.code()) {

            ProjectBeanHolder.getCacheService().set(AccountPhoneNumberVerifyCache.AccountServiceKey, verify.username(), new AccountPhoneNumberVerifyCache(verify.code(), DateUtils.now(), verify.type(), true), ProjectBeanHolder.getSecurityConfiguration().getHumanValidationExpire());

            return new PhoneNumberSmsVerifyResult(PhoneNumberSmsVerifyResultStatus.SUCCESS);
        }

        return new PhoneNumberSmsVerifyResult(PhoneNumberSmsVerifyResultStatus.FAILED);
    }

    public int getSmsCode(String key, PhoneNumberSmsType type) {

        AccountPhoneNumberVerifyCache cache = ProjectBeanHolder.getCacheService().get(AccountPhoneNumberVerifyCache.AccountServiceKey, key, AccountPhoneNumberVerifyCache.class);

        if(cache != null) {

            if(cache.type() == type) {

                return cache.code();
            }

            return 0;
        }

        return 0;
    }

    public UserRegisterResult register(UserRegisterForm form) {

        if(ProjectBeanHolder.getSecurityConfiguration().isPhoneNumberVerifyEnable()) {

            AccountPhoneNumberVerifyCache cache = ProjectBeanHolder.getCacheService().get(AccountPhoneNumberVerifyCache.AccountServiceKey, form.getUsername(), AccountPhoneNumberVerifyCache.class);

            if (cache != null) {

                if (cache.type() == PhoneNumberSmsType.ACCOUNT_REGISTER && cache.verified()) {

                    return register(form.getEntity());
                }

                return new UserRegisterResult(UserRegisterResultStatus.VERIFICATION_FAILED);
            }

            return new UserRegisterResult(UserRegisterResultStatus.REJECTED);
        }

        return register(form.getEntity());
    }

    public UserRegisterResult register(User user) {

        UserRepository repository = ProjectBeanHolder.getUserRepository();

        SecurityConfiguration configuration = ProjectBeanHolder.getSecurityConfiguration();

        List<UserRegisterFormField> fields = new ArrayList<>();

        if(!user.getUsername().matches(configuration.getUsernameFormatRegex()) || repository.existsById(user.getUsername())) {

            fields.add(UserRegisterFormField.USERNAME);
        }

        if(!user.getPassword().matches(configuration.getPasswordFormatRegex())) {

            fields.add(UserRegisterFormField.PASSWORD);
        }

        String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

        if(!user.getEmail().matches(emailRegex) || repository.countByEmailEquals(user.getEmail()) >= configuration.getEmailMaxUse()) {

            fields.add(UserRegisterFormField.EMAIL);
        }

        if(user.getPhoneNumber() == null || repository.countByPhoneNumberContaining(user.getPhoneNumber()) >= configuration.getPhoneNumberMaxUse()) {

            fields.add(UserRegisterFormField.PHONE_NUMBER);
        }

        if(user.getFirstName() == null || user.getFirstName().length() < 1) {

            fields.add(UserRegisterFormField.FIRST_NAME);
        }

        if(user.getLastName() == null || user.getLastName().length() < 1) {

            fields.add(UserRegisterFormField.LAST_NAME);
        }

        if(fields.size() > 0) {

            return new UserRegisterResult(UserRegisterResultStatus.UNACCEPTABLE_CONTENT, fields);
        }

        repository.save(user);

        ProjectBeanHolder.getCacheService().evictIfPresent(AccountPhoneNumberVerifyCache.AccountServiceKey, user.getUsername());

        return new UserRegisterResult(UserRegisterResultStatus.SUCCESS);
    }



}
