package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;

public enum StoreCheckInPageDeatilResultStatus implements ReasonableStatus {

    SUCCESS(200),
    SERVICE_DOWN(400);

    private final int code;

    private StoreCheckInPageDeatilResultStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

