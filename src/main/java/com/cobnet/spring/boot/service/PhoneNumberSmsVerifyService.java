package com.cobnet.spring.boot.service;

import com.cobnet.common.DateUtils;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsRequestResultStatus;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsType;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsVerifyResultStatus;
import com.cobnet.spring.boot.service.support.AccountPhoneNumberVerifyCache;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@Service
public class PhoneNumberSmsVerifyService {

    public ResponseResult<PhoneNumberSmsRequestResultStatus> request(PhoneNumberSmsRequest request) throws IOException {

        HttpSession session = Objects.requireNonNull(ProjectBeanHolder.getCurrentHttpRequest()).getSession(true);

        if(ProjectBeanHolder.getSecurityConfiguration().isHumanValidationEnable() && !ProjectBeanHolder.getHumanValidator().isValidated(session.getId())) {

            return new ResponseResult<>(PhoneNumberSmsRequestResultStatus.HUMAN_VALIDATION_REQUEST);
        }

        if(ProjectBeanHolder.getSecurityConfiguration().isSessionLimitEnable() && ProjectBeanHolder.getSecurityConfiguration().getSessionCreatedTimeRequire().compareTo(DateUtils.getInterval(new Date(session.getCreationTime()), DateUtils.now())) > 0) {

            return new ResponseResult<>(PhoneNumberSmsRequestResultStatus.REJECTED);
        }

        if(request.type() == PhoneNumberSmsType.ACCOUNT_REGISTER) {

            long count = ProjectBeanHolder.getUserRepository().countByPhoneNumberContaining(request.phoneNumber());

            if(count > ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberMaxUse()) {

                return new ResponseResult<>(PhoneNumberSmsRequestResultStatus.NUMBER_OVERUSED);
            }
        }

        AccountPhoneNumberVerifyCache cache = ProjectBeanHolder.getCacheService().get(AccountPhoneNumberVerifyCache.PhoneNumberSmsVerifyServiceKey, request.username(), AccountPhoneNumberVerifyCache.class);

        if(cache != null) {

            if (DateUtils.addDuration(cache.createdTime(), ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsGenerateInterval()).before(DateUtils.now())) {

                if(cache.times() >= ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsVerifyTimes()) {

                    return new ResponseResult<>(PhoneNumberSmsRequestResultStatus.EXHAUSTED);
                }

                return send(request);
            }

            return new ResponseResult<>(PhoneNumberSmsRequestResultStatus.INTERVAL_LIMITED, new ObjectWrapper<>("time-remain", ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsGenerateInterval().minus(DateUtils.getInterval(DateUtils.now(), cache.createdTime()))));
        }

        return send(request);
    }

    private ResponseResult<PhoneNumberSmsRequestResultStatus> send(PhoneNumberSmsRequest request) {

        Random random = new Random();

        int code = random.nextInt(987654 + 1 - 123456) + 123456;

        AccountPhoneNumberVerifyCache cache = this.getCache(request.username());

        ProjectBeanHolder.getCacheService().set(AccountPhoneNumberVerifyCache.PhoneNumberSmsVerifyServiceKey, request.username(), new AccountPhoneNumberVerifyCache(code, DateUtils.now(), request.type(), cache != null ? cache.times() + 1 : 0, false), ProjectBeanHolder.getSecurityConfiguration().getHumanValidationExpire());

        try {

            ProjectBeanHolder.getMessager().message(request.phoneNumber(), ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberVerifySmsMessage().replace("$(code)", Integer.toString(code))).create();

        } catch (Exception ex) {

            return new ResponseResult<>(PhoneNumberSmsRequestResultStatus.SERVICE_DOWN);
        }

        return new ResponseResult<>(PhoneNumberSmsRequestResultStatus.SUCCESS);
    }

    public ResponseResult<PhoneNumberSmsVerifyResultStatus> verify(PhoneNumberSmsVerify verify) {

        AccountPhoneNumberVerifyCache cache = getCache(verify.username());

        if(cache != null && cache.type() == verify.type() && cache.code() == verify.code()) {

            ProjectBeanHolder.getCacheService().set(AccountPhoneNumberVerifyCache.PhoneNumberSmsVerifyServiceKey, verify.username(), new AccountPhoneNumberVerifyCache(verify.code(), DateUtils.now(), verify.type(), cache.times(),true), ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsCodeExpire());

            return new ResponseResult<>(PhoneNumberSmsVerifyResultStatus.SUCCESS);
        }

        return new ResponseResult<>(PhoneNumberSmsVerifyResultStatus.FAILED);
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

    public boolean delete(String key) {

        return ProjectBeanHolder.getCacheService().evictIfPresent(AccountPhoneNumberVerifyCache.PhoneNumberSmsVerifyServiceKey, key);
    }
}
