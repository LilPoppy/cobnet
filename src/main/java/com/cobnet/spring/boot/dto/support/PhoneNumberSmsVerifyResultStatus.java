package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;

public enum PhoneNumberSmsVerifyResultStatus implements ReasonableStatus {

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
