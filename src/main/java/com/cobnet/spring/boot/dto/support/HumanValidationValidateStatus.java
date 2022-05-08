package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum HumanValidationValidateStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.CREATED, null),
    TIMEOUT(HttpStatus.REQUEST_TIMEOUT, null),
    WRONG_POSITION(HttpStatus.BAD_REQUEST, null),
    REJECTED(HttpStatus.BAD_REQUEST, null);

    private final HttpStatus status;

    private final String message;

    private HumanValidationValidateStatus(HttpStatus status, String message) {
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
