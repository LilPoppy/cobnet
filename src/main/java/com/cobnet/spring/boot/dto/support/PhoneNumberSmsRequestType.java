package com.cobnet.spring.boot.dto.support;

public enum PhoneNumberSmsRequestType {

    ACCOUNT_REGISTER(0),
    CHANGE_PASSWORD(1);

    private final int code;

    private PhoneNumberSmsRequestType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
