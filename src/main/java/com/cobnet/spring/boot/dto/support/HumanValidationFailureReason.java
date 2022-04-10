package com.cobnet.spring.boot.dto.support;

public enum HumanValidationFailureReason {

    NONE(0),
    TIMEOUT(1),
    WRONG_POSITION(2);

    private int code;

    private HumanValidationFailureReason(int code) {
        this.code = code;
    }
}
