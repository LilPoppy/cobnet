package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;

public enum StoreRegisterResultStatus implements ReasonableStatus {

    SUCCESS(201),
    STORE_NONEXISTENT(400),
    EXHAUSTED(400),
    STORE_PERMANENTLY_CLOSED(400),
    STORE_ALREADY_REGISTERED(400),
    SECURITY_CHECK(403),
    HUMAN_VALIDATION_REQUEST(400),
    SERVICE_DOWN(502);

    private final int code;

    private StoreRegisterResultStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
