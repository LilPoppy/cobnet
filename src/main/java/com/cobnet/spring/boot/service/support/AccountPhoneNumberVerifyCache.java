package com.cobnet.spring.boot.service.support;

import com.cobnet.spring.boot.dto.support.PhoneNumberSmsType;
import com.cobnet.spring.boot.service.AccountService;

import java.io.Serializable;
import java.util.Date;

public record AccountPhoneNumberVerifyCache(int code, Date createdTime, PhoneNumberSmsType type, int times, boolean verified) implements Serializable {

    public static final String AccountServiceKey = AccountService.class.getSimpleName();

    @Override
    public int code() {
        return code;
    }

    @Override
    public Date createdTime() {
        return createdTime;
    }

    public PhoneNumberSmsType type() { return type; }

    public int times() {
        return times;
    }

    @Override
    public boolean verified() {
        return verified;
    }
}
