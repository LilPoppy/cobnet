package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;

public enum StoreSearchResultStatus implements ReasonableStatus {

    SUCCESS(200),
    EXHAUSTED(400),
    SERVICE_DOWN(502);

    private final int code;

    private StoreSearchResultStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
