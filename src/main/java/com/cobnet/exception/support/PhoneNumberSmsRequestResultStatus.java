package com.cobnet.exception.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum PhoneNumberSmsRequestResultStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.CREATED, null),
    INTERVAL_LIMITED(HttpStatus.TOO_MANY_REQUESTS, "Operation too frequent, please try again later."),
    NUMBER_OVERUSED(HttpStatus.CONFLICT, "Phone number is over limited use.");

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
