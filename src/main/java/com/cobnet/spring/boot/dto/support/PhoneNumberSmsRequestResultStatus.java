package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum PhoneNumberSmsRequestResultStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.CREATED),
    INTERVAL_LIMITED(HttpStatus.BAD_REQUEST),
    EXHAUSTED(HttpStatus.BAD_REQUEST),
    HUMAN_VALIDATION_REQUEST(HttpStatus.BAD_REQUEST),
    NUMBER_OVERUSED(HttpStatus.CONFLICT),
    SERVICE_DOWN(HttpStatus.SERVICE_UNAVAILABLE),
    REJECTED(HttpStatus.BAD_REQUEST);

    private final HttpStatus status;

    private PhoneNumberSmsRequestResultStatus(HttpStatus status) {
        this.status = status;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }
}
