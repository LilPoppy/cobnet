package com.cobnet.spring.boot.service;

import com.cobnet.common.DateUtils;
import com.cobnet.common.KeyValuePair;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.cobnet.spring.boot.dto.support.HumanValidationRequestType;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsRequestResultStatus;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsType;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsVerifyResultStatus;
import com.cobnet.spring.boot.service.support.AccountPhoneNumberVerifyCache;
import com.cobnet.spring.boot.service.support.HumanValidationCache;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;

@Service
public class PhoneNumberSmsVerifyService {

    public PhoneNumberSmsRequestResult request(PhoneNumberSmsRequest request) throws IOException {

        if(request.type() == PhoneNumberSmsType.ACCOUNT_REGISTER) {

            long count = ProjectBeanHolder.getUserRepository().countByPhoneNumberContaining(request.phoneNumber());

            if(count > ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberMaxUse()) {

                return new PhoneNumberSmsRequestResult(PhoneNumberSmsRequestResultStatus.NUMBER_OVERUSED);
            }
        }

        AccountPhoneNumberVerifyCache cache = ProjectBeanHolder.getCacheService().get(AccountPhoneNumberVerifyCache.PhoneNumberSmsVerifyServiceKey, request.username(), AccountPhoneNumberVerifyCache.class);

        if(cache != null) {

            if (DateUtils.addDuration(cache.createdTime(), ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsGenerateInterval()).before(DateUtils.now())) {

                if(cache.times() >= ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsVerifyTimes()) {

                    HumanValidationCache humanValidationCache = ProjectBeanHolder.getHumanValidator().getCache(request.phoneNumber());

                    if(humanValidationCache == null || !humanValidationCache.isValidated()) {

                        return new PhoneNumberSmsRequestResult(PhoneNumberSmsRequestResultStatus.HUMAN_VALIDATION_REQUEST, new Object[]{ new KeyValuePair<>(HttpMethod.POST, "/visitor/human-validate/request"), new HumanValidationRequest(HumanValidationRequestType.SMS_REQUEST, request.username(), request.phoneNumber())});
                    }
                }

                return send(request);
            }

            return new PhoneNumberSmsRequestResult(PhoneNumberSmsRequestResultStatus.INTERVAL_LIMITED, ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsGenerateInterval().minus(DateUtils.getInterval(DateUtils.now(), cache.createdTime())));
        }

        return send(request);
    }

    private PhoneNumberSmsRequestResult send(PhoneNumberSmsRequest request) {

        Random random = new Random();

        int code = random.nextInt(987654 + 1 - 123456) + 123456;

        AccountPhoneNumberVerifyCache cache = this.getCache(request.username());

        ProjectBeanHolder.getCacheService().set(AccountPhoneNumberVerifyCache.PhoneNumberSmsVerifyServiceKey, request.username(), new AccountPhoneNumberVerifyCache(code, DateUtils.now(), request.type(), cache != null ? cache.times() + 1 : 0, false), ProjectBeanHolder.getSecurityConfiguration().getHumanValidationExpire());

        try {

            ProjectBeanHolder.getMessager().message(request.phoneNumber(), ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberVerifySmsMessage().replace("$(code)", Integer.toString(code))).create();

        } catch (Exception ex) {

            return new PhoneNumberSmsRequestResult(PhoneNumberSmsRequestResultStatus.SERVICE_DOWN);
        }

        return new PhoneNumberSmsRequestResult(PhoneNumberSmsRequestResultStatus.SUCCESS);
    }

    public PhoneNumberSmsVerifyResult request(PhoneNumberSmsVerify verify) {

        AccountPhoneNumberVerifyCache cache = getCache(verify.username());

        if(cache != null && cache.type() == verify.type() && cache.code() == verify.code()) {

            ProjectBeanHolder.getCacheService().set(AccountPhoneNumberVerifyCache.PhoneNumberSmsVerifyServiceKey, verify.username(), new AccountPhoneNumberVerifyCache(verify.code(), DateUtils.now(), verify.type(), cache.times(),true), ProjectBeanHolder.getSecurityConfiguration().getHumanValidationExpire());

            return new PhoneNumberSmsVerifyResult(PhoneNumberSmsVerifyResultStatus.SUCCESS);
        }

        return new PhoneNumberSmsVerifyResult(PhoneNumberSmsVerifyResultStatus.FAILED);
    }

    public AccountPhoneNumberVerifyCache getCache(String key) {

        return ProjectBeanHolder.getCacheService().get(AccountPhoneNumberVerifyCache.PhoneNumberSmsVerifyServiceKey, key, AccountPhoneNumberVerifyCache.class);
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
}
