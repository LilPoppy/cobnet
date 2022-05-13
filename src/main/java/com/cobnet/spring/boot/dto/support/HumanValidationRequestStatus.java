package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum HumanValidationRequestStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.CREATED, null),
    INTERVAL_LIMITED(HttpStatus.TOO_MANY_REQUESTS, "Operation too frequent, please try again later."),
    VALIDATED(HttpStatus.BAD_REQUEST, "You're already validated, please try your request again.");


    private final HttpStatus status;

    private final String message;

    private HumanValidationRequestStatus(HttpStatus status, String message) {
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
