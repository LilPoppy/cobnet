package com.cobnet.spring.boot.dto.support;

public enum PhoneNumberSmsRequestResultStatus {

    SUCCESS(201),
    INTERVAL_LIMITED(400),
    NUMBER_OVERUSED(409),
    SERVICE_DOWN(503);

    private final int code;

    private PhoneNumberSmsRequestResultStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
