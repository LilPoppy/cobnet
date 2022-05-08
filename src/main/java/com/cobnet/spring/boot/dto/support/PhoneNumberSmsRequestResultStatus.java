package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum PhoneNumberSmsRequestResultStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.CREATED, null),
    INTERVAL_LIMITED(HttpStatus.BAD_REQUEST, null),
    EXHAUSTED(HttpStatus.BAD_REQUEST, null),
    NUMBER_OVERUSED(HttpStatus.CONFLICT, null),
    SERVICE_DOWN(HttpStatus.SERVICE_UNAVAILABLE, null),
    REJECTED(HttpStatus.BAD_REQUEST, null);

    private final HttpStatus status;

    private final String message;

    private PhoneNumberSmsRequestResultStatus(HttpStatus status, String message) {
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
