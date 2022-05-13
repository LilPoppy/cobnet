package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum HumanValidationValidateStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.CREATED, null),
    TIMEOUT(HttpStatus.NO_CONTENT, "No result is present in our record."),
    WRONG_POSITION(HttpStatus.BAD_REQUEST, "Argument provided is not match any in our record.");

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
