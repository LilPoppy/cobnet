package com.cobnet.spring.boot.dto.support;

public enum HumanValidationRequireResult {

    SUCCESS(0),
    INTERVAL_LIMITED(1);

    private int code;

    private HumanValidationRequireResult(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
