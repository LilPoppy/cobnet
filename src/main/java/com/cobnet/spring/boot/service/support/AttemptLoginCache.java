package com.cobnet.spring.boot.service.support;

import com.cobnet.spring.boot.service.AccountService;

import java.io.Serializable;
import java.util.Date;

public record AttemptLoginCache(Date createdTime, int times) implements Serializable {

    public static final String AccountServiceName = AccountService.class.getSimpleName();

    @Override
    public Date createdTime() {
        return createdTime;
    }

    public int times() {
        return times;
    }
}
