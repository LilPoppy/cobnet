package com.cobnet.spring.boot.service.support;

import com.cobnet.interfaces.CacheValue;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsType;
import com.cobnet.spring.boot.service.PhoneNumberSmsVerifyService;

import java.io.Serializable;
import java.util.Date;

public record AccountPhoneNumberVerifyCache(int code, Date creationTime, PhoneNumberSmsType type, int count, boolean verified) implements CacheValue {

    public static final String PhoneNumberSmsVerifyServiceKey = PhoneNumberSmsVerifyService.class.getSimpleName();

    @Override
    public int code() {
        return code;
    }

    public Date creationTime() {
        return creationTime;
    }

    public PhoneNumberSmsType type() { return type; }

    public int count() {
        return count;
    }

    @Override
    public boolean verified() {
        return verified;
    }
}
