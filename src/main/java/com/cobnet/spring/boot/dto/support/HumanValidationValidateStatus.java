package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum HumanValidationValidateStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.CREATED),
    TIMEOUT(HttpStatus.REQUEST_TIMEOUT),
    WRONG_POSITION(HttpStatus.BAD_REQUEST),
    REJECTED(HttpStatus.BAD_REQUEST);

    private final HttpStatus status;

    private HumanValidationValidateStatus(HttpStatus status) {
        this.status = status;
    }


    @Override
    public HttpStatus getStatus() {
        return this.status;
    }
}
