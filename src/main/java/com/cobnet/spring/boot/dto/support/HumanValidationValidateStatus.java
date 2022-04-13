package com.cobnet.spring.boot.dto.support;

public enum HumanValidationValidateStatus {

    SUCCESS(201),
    TIMEOUT(408),
    WRONG_POSITION(400),
    REJECTED(400);

    private final int code;

    private HumanValidationValidateStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
