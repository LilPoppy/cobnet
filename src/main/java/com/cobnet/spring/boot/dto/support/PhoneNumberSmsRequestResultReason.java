package com.cobnet.spring.boot.dto.support;

public enum PhoneNumberSmsRequestResultReason {

    SUCCESS(201),
    INTERVAL_LIMITED(400),
    NUMBER_OVERUSED(409),
    SERVICE_DOWN(503);

    private final int code;

    private PhoneNumberSmsRequestResultReason(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
