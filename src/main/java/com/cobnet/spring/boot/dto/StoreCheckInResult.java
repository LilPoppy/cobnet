package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.spring.boot.dto.support.StoreCheckInResultStatus;

public record StoreCheckInResult(StoreCheckInResultStatus status) implements ApplicationJson {

    @Override
    public StoreCheckInResultStatus status() {
        return status;
    }
}
