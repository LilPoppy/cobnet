package com.cobnet.spring.boot.service;

import com.cobnet.common.DateUtils;
import com.cobnet.exception.ResponseFailureStatusException;
import com.cobnet.interfaces.spring.repository.AccountPhoneNumberVerifyCacheRepository;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsRequestResultStatus;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsType;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsVerifyResultStatus;
import com.cobnet.spring.boot.cache.AccountPhoneNumberVerifyCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

@Service
public class PhoneNumberSmsVerifyService {

    @Autowired
    private AccountPhoneNumberVerifyCacheRepository repository;

    public boolean requestSms(String username, String phoneNumber, PhoneNumberSmsType type) throws IOException, ResponseFailureStatusException {

        if(type == PhoneNumberSmsType.ACCOUNT_REGISTER) {

            long count = ProjectBeanHolder.getUserRepository().countByPhoneNumberContaining(phoneNumber);

            if(count > ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberMaxUse()) {

                throw new ResponseFailureStatusException(PhoneNumberSmsRequestResultStatus.NUMBER_OVERUSED);
            }
        }

        AccountPhoneNumberVerifyCache cache = this.getCache(username);

        if(cache != null) {

            if (DateUtils.addDuration(cache.getCreationTime(), ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsGenerateInterval()).before(DateUtils.now())) {

                if(cache.getCount() >= ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsVerifyTimes()) {

                    throw  new ResponseFailureStatusException(PhoneNumberSmsRequestResultStatus.EXHAUSTED);
                }

                return send(username, phoneNumber, type);
            }

            throw new ResponseFailureStatusException(PhoneNumberSmsRequestResultStatus.INTERVAL_LIMITED, new ObjectWrapper<>("time-remain", ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsGenerateInterval().minus(DateUtils.getInterval(DateUtils.now(), cache.getCreationTime()))));
        }

        return send(username, phoneNumber, type);
    }

    private boolean send(String username, String phoneNumber, PhoneNumberSmsType type) throws ResponseFailureStatusException {

        Random random = new Random();

        int code = random.nextInt(987654 + 1 - 123456) + 123456;

        AccountPhoneNumberVerifyCache cache = this.getCache(username);

        repository.save(new AccountPhoneNumberVerifyCache(username, code, DateUtils.now(), type, cache != null ? cache.getCount() + 1 : 0, false), ProjectBeanHolder.getSecurityConfiguration().getHumanValidation().getExpire());

        try {

            ProjectBeanHolder.getMessager().message(phoneNumber, ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberVerifySmsMessage().replace("$(code)", Integer.toString(code))).create();

        } catch (Exception ex) {

            throw new ResponseFailureStatusException(PhoneNumberSmsRequestResultStatus.SERVICE_DOWN);
        }

        return true;
    }

    public boolean verify(PhoneNumberSmsVerify verify) throws ResponseFailureStatusException {

        AccountPhoneNumberVerifyCache cache = getCache(verify.username());

        if(cache != null && cache.getType() == verify.type() && cache.getCode() == verify.code()) {

            repository.save(new AccountPhoneNumberVerifyCache(verify.username(), verify.code(), DateUtils.now(), verify.type(), cache.getCount(), true), ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsCodeExpire());

            return true;
        }

        throw new ResponseFailureStatusException(PhoneNumberSmsVerifyResultStatus.FAILED);
    }

    public AccountPhoneNumberVerifyCache getCache(String key) {

        Optional<AccountPhoneNumberVerifyCache> optional = repository.findById(key);

        if(optional.isEmpty()) {

            return null;
        }

        return optional.get();
    }

    public int getCode(String key, PhoneNumberSmsType type) {

        AccountPhoneNumberVerifyCache cache = getCache(key);

        if(cache != null) {

            if(cache.getType() == type) {

                return cache.getCode();
            }

            return 0;
        }

        return 0;
    }

    public void delete(String key) {

        repository.deleteById(key);
    }
}
