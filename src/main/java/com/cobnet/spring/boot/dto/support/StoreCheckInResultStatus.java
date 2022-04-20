package com.cobnet.spring.boot.dto.support;

public enum StoreCheckInResultStatus {

    SUCCESS(200);

    private int code;

    private StoreCheckInResultStatus(int code) {

        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
