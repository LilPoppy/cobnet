package com.cobnet.spring.boot.dto.support;

public enum HumanValidationRequestStatus {

    SUCCESS(201),
    INTERVAL_LIMITED(400);

    private final int code;

    private HumanValidationRequestStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
