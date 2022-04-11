package com.cobnet.spring.boot.service.support;

import com.cobnet.spring.boot.dto.support.PhoneNumberSmsRequestType;
import com.cobnet.spring.boot.service.AccountService;

import java.io.Serializable;
import java.util.Date;

public record AccountPhoneNumberVerifyCache(int code, Date createdTime, PhoneNumberSmsRequestType type) implements Serializable {

    public static final String AccountServiceKey = AccountService.class.getSimpleName();

    @Override
    public int code() {
        return code;
    }

    @Override
    public Date createdTime() {
        return createdTime;
    }

    public PhoneNumberSmsRequestType type() { return type; }
}
