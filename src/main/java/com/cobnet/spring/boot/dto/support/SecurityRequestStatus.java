package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum SecurityRequestStatus implements ReasonableStatus {

    SECURITY_FORBIDDEN_SESSION(HttpStatus.FORBIDDEN, null),
    SECURITY_FORBIDDEN_HUMAN_VALIDATION(HttpStatus.FORBIDDEN, null),
    SECURITY_MAX_MESSAGE(HttpStatus.FORBIDDEN, null),
    SECURITY_MAXIMUM_SESSION(HttpStatus.BAD_REQUEST, null);

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
