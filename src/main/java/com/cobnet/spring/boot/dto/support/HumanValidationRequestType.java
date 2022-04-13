package com.cobnet.spring.boot.dto.support;

public enum HumanValidationRequestType {

    LOGIN(0),
    SMS_REQUEST(1);

    private final int code;

    private HumanValidationRequestType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
