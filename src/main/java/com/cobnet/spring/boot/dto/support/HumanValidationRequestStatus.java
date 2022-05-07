package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum HumanValidationRequestStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.CREATED),
    INTERVAL_LIMITED(HttpStatus.BAD_REQUEST),
    VALIDATED(HttpStatus.BAD_REQUEST),
    REJECTED(HttpStatus.BAD_REQUEST);


    private final HttpStatus status;

    private HumanValidationRequestStatus(HttpStatus status) {
        this.status = status;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }
}
