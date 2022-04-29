package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;

public enum StoreCheckInPageDetailResultStatus implements ReasonableStatus {

    SUCCESS(200),
    NO_EXIST(404),
    SERVICE_DOWN(400);

    private final int code;

    private StoreCheckInPageDetailResultStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

