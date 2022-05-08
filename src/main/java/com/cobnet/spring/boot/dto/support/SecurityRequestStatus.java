package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum SecurityRequestStatus implements ReasonableStatus {

    SESSION_FORBIDDEN(HttpStatus.FORBIDDEN, null),
    HUMAN_VALIDATION_REQUIRED(HttpStatus.FORBIDDEN, null);

    private HttpStatus status;

    private String message;

    private SecurityRequestStatus(HttpStatus status, String message) {

        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String message() {
        return message;
    }
}
