package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;

public enum AuthenticationStatus implements ReasonableStatus {

    SUCCESS(200),
    USER_NOT_FOUND(401),
    PASSWORD_NOT_MATCH(401),
    HUMAN_VALIDATION_REQUEST(400),
    REACHED_MAXIMUM_ATTEMPT(400),
    LOCKED(400),
    REJECTED(400);

    private final int code;

    private AuthenticationStatus(int status) {

        this.code = status;
    }

    @Override
    public int getCode() {

        return this.code;
    }
}
