package com.cobnet.spring.boot.dto.support;

public enum PhoneNumberSmsVerifyResultStatus {

    SUCCESS(200),
    FAILED(403);

    private final int code;

    private PhoneNumberSmsVerifyResultStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
