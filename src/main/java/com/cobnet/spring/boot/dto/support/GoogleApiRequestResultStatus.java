package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum GoogleApiRequestResultStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.OK, null),
    FAILED(HttpStatus.BAD_REQUEST, null),
    EXHAUSTED(HttpStatus.BAD_REQUEST, null),
    REJECTED(HttpStatus.BAD_REQUEST, null),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, null),
    SERVICE_DOWN(HttpStatus.BAD_GATEWAY, null);

    private final HttpStatus status;

    private final String message;

    private GoogleApiRequestResultStatus(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }

    @Override
    public String message() {
        return message;
    }
}
