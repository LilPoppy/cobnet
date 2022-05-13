package com.cobnet.spring.boot.service;

import com.cobnet.common.DateUtils;
import com.cobnet.exception.ResponseFailureStatusException;
import com.cobnet.interfaces.spring.repository.AccountPhoneNumberVerifyCacheRepository;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.cobnet.exception.support.PhoneNumberSmsRequestResultStatus;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsType;
import com.cobnet.exception.support.PhoneNumberSmsVerifyResultStatus;
import com.cobnet.spring.boot.cache.AccountPhoneNumberVerifyCache;
import com.cobnet.exception.support.ServiceRequestStatus;
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

            if(cache.getCount() >= ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsVerifyInitialCount()) {

                if(cache.getCount() > ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsVerifyMaxCount()) {

                    throw new ResponseFailureStatusException(ServiceRequestStatus.EXHAUSTED);
                }

                if (DateUtils.addDuration(cache.getCreationTime(), ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsGenerateInterval()).before(DateUtils.now())) {

                    throw new ResponseFailureStatusException(PhoneNumberSmsRequestResultStatus.INTERVAL_LIMITED, new MessageWrapper("time-remain", ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsGenerateInterval().minus(DateUtils.getInterval(DateUtils.now(), cache.getCreationTime()))));
                }
            }
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

            throw new ResponseFailureStatusException(ServiceRequestStatus.SERVICE_DOWN);
        }

        return true;
    }

    public boolean verify(PhoneNumberSmsVerify verify) throws ResponseFailureStatusException {

        AccountPhoneNumberVerifyCache cache = getCache(verify.username());

        if(cache != null && cache.getType() == verify.type() && cache.getCode() == verify.code()) {

            repository.save(new AccountPhoneNumberVerifyCache(verify.username(), verify.code(), DateUtils.now(), verify.type(), cache.getCount(), true), ProjectBeanHolder.getSecurityConfiguration().getPhoneNumberSmsCodeExpire());

            return true;
        }

        throw new ResponseFailureStatusException(PhoneNumberSmsVerifyResultStatus.UNMATCHED);
    }

    public AccountPhoneNumberVerifyCache getCache(String key) {

        Optional<AccountPhoneNumberVerifyCache> optional = repository.findById(key);

        if(optional.isEmpty()) {

            return null;
        }

        return optional.get();
    }

    public void delete(String key) {

        repository.deleteById(key);
    }
}
