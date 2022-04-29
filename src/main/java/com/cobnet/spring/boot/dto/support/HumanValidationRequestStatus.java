package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;

public enum HumanValidationRequestStatus implements ReasonableStatus {

    SUCCESS(201),
    INTERVAL_LIMITED(400),
    VALIDATED(400),
    REJECTED(400);


    private final int code;

    private HumanValidationRequestStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
