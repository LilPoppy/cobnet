package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;

public enum StoreCheckInResultStatus implements ReasonableStatus {

    SUCCESS(200);

    private int code;

    private StoreCheckInResultStatus(int code) {

        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
