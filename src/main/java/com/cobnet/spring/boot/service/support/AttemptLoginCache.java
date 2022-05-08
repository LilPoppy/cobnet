package com.cobnet.spring.boot.service.support;

import com.cobnet.interfaces.CacheValue;
import com.cobnet.spring.boot.service.AccountService;

import java.util.Date;

public record AttemptLoginCache(Date creationTime, int count) implements CacheValue {

    public static final String AccountServiceName = AccountService.class.getSimpleName();

    @Override
    public Date creationTime() {
        return creationTime;
    }

    public int count() {
        return count;
    }

}
