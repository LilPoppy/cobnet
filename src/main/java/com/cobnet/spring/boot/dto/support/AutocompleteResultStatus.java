package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;

public enum AutocompleteResultStatus implements ReasonableStatus {

    SUCCESS(200),
    EXHAUSTED(400),
    REJECTED(400),
    HUMAN_VALIDATION_REQUEST(400),
    SERVICE_DOWN(502);

    private final int code;

    private AutocompleteResultStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
