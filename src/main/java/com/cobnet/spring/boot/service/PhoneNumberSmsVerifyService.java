package com.cobnet.spring.boot.service;

import com.cobnet.common.DateUtils;
import com.cobnet.exception.ResponseFailureStatusException;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsRequestResultStatus;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsType;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsVerifyResultStatus;
import com.cobnet.spring.boot.service.support.AccountPhoneNumberVerifyCache;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

@Service
public class PhoneNumberSmsVerifyService {

    public boolean requestSms(String username, String phoneNumber, PhoneNumberSmsType type) throws IOException, ResponseFailureStatusException {

        if(type == PhoneNumberSmsType.ACCOUNT_REGISTER) {

            long count = ProjectBeanHolder.getUserRepository().countByPhoneNumberContaining(phoneNumber);

            if(count > ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberMaxUse()) {

                throw new ResponseFailureStatusException(PhoneNumberSmsRequestResultStatus.NUMBER_OVERUSED);
            }
        }

        AccountPhoneNumberVerifyCache cache = this.getCache(username);

        if(cache != null) {

            if (DateUtils.addDuration(cache.creationTime(), ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsGenerateInterval()).before(DateUtils.now())) {

                if(cache.count() >= ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsVerifyTimes()) {

                    throw  new ResponseFailureStatusException(PhoneNumberSmsRequestResultStatus.EXHAUSTED);
                }

                return send(username, phoneNumber, type);
            }

            throw new ResponseFailureStatusException(PhoneNumberSmsRequestResultStatus.INTERVAL_LIMITED, new ObjectWrapper<>("time-remain", ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsGenerateInterval().minus(DateUtils.getInterval(DateUtils.now(), cache.creationTime()))));
        }

        return send(username, phoneNumber, type);
    }

    private boolean send(String username, String phoneNumber, PhoneNumberSmsType type) throws ResponseFailureStatusException {

        Random random = new Random();

        int code = random.nextInt(987654 + 1 - 123456) + 123456;

        AccountPhoneNumberVerifyCache cache = this.getCache(username);

        ProjectBeanHolder.getCacheService().set(AccountPhoneNumberVerifyCache.PhoneNumberSmsVerifyServiceKey, username, new AccountPhoneNumberVerifyCache(code, DateUtils.now(), type, cache != null ? cache.count() + 1 : 0, false), ProjectBeanHolder.getSecurityConfiguration().getHumanValidationExpire());

        try {

            ProjectBeanHolder.getMessager().message(phoneNumber, ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberVerifySmsMessage().replace("$(code)", Integer.toString(code))).create();

        } catch (Exception ex) {

            throw new ResponseFailureStatusException(PhoneNumberSmsRequestResultStatus.SERVICE_DOWN);
        }

        return true;
    }

    public boolean verify(PhoneNumberSmsVerify verify) throws ResponseFailureStatusException {

        AccountPhoneNumberVerifyCache cache = getCache(verify.username());

        if(cache != null && cache.type() == verify.type() && cache.code() == verify.code()) {

            ProjectBeanHolder.getCacheService().set(AccountPhoneNumberVerifyCache.PhoneNumberSmsVerifyServiceKey, verify.username(), new AccountPhoneNumberVerifyCache(verify.code(), DateUtils.now(), verify.type(), cache.count(),true), ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsCodeExpire());

            return true;
        }

        throw new ResponseFailureStatusException(PhoneNumberSmsVerifyResultStatus.FAILED);
    }

    public AccountPhoneNumberVerifyCache getCache(String key) {

        return ProjectBeanHolder.getCacheService().get(AccountPhoneNumberVerifyCache.PhoneNumberSmsVerifyServiceKey, AccountPhoneNumberVerifyCache.class, key);
    }

    public int getCode(String key, PhoneNumberSmsType type) {

        AccountPhoneNumberVerifyCache cache = getCache(key);

        if(cache != null) {

            if(cache.type() == type) {

                return cache.code();
            }

            return 0;
        }

        return 0;
    }

    public boolean delete(String key) {

        return ProjectBeanHolder.getCacheService().evictIfPresent(AccountPhoneNumberVerifyCache.PhoneNumberSmsVerifyServiceKey, key);
    }
}
