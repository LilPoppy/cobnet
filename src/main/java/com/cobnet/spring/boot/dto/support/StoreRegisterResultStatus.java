package com.cobnet.spring.boot.dto.support;

public enum StoreRegisterResultStatus {

    SUCCESS(201),
    STORE_NONEXISTENT(400),
    STORE_PERMANENTLY_CLOSED(400),
    STORE_EXISTED(400),
    SERVICE_DOWN(502);

    private final int code;

    private StoreRegisterResultStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
