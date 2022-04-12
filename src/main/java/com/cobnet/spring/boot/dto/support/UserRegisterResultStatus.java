package com.cobnet.spring.boot.dto.support;

public enum UserRegisterResultStatus {

    SUCCESS(201),
    UNACCEPTABLE_CONTENT(400),
    VERIFICATION_FAILED(408),
    REJECTED(400);

    private final int code;

    private UserRegisterResultStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
