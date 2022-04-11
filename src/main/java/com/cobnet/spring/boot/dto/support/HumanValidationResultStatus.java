package com.cobnet.spring.boot.dto.support;

public enum HumanValidationResultStatus {

    SUCCESS(201),
    TIMEOUT(408),
    WRONG_POSITION(400);

    private final int code;

    private HumanValidationResultStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
