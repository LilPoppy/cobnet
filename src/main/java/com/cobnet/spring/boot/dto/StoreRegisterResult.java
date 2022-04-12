package com.cobnet.spring.boot.dto;

import com.cobnet.spring.boot.dto.support.StoreRegisterResultStatus;

public record StoreRegisterResult(StoreRegisterResultStatus status) {

    @Override
    public StoreRegisterResultStatus status() {
        return status;
    }
}
