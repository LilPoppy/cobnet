package com.cobnet.spring.boot.dto.support;

public enum HumanValidationFailureReason {

    NONE(0),
    INVAILD_PARAMETER(1),
    TIMEOUT(2),
    WRONG_POSITION(3);

    private int code;

    private HumanValidationFailureReason(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
