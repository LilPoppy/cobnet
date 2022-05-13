package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum PhoneNumberSmsVerifyResultStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.OK, null),
    UNMATCHED(HttpStatus.NOT_ACCEPTABLE, "Code is not match our record.");

    private final HttpStatus status;

    private final String message;

    private PhoneNumberSmsVerifyResultStatus(HttpStatus status, String message) {
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
