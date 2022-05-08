package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum UserRegisterResultStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.CREATED, null),
    UNACCEPTABLE_CONTENT(HttpStatus.BAD_REQUEST, null),
    VERIFICATION_FAILED(HttpStatus.REQUEST_TIMEOUT, null),
    REJECTED(HttpStatus.BAD_REQUEST, null);

    private final HttpStatus status;

    private final String message;

    private UserRegisterResultStatus(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }

    @Override
    public String message() {
        return null;
    }
}
