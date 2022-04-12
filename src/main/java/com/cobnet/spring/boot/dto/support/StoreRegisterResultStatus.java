package com.cobnet.spring.boot.dto.support;

public enum StoreRegisterResultStatus {

    SUCCESS(201),
    STORE_EXISTED(400);

    private final int code;

    private StoreRegisterResultStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
