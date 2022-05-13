package com.cobnet.exception.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum ServiceRequestStatus implements ReasonableStatus {

    EXHAUSTED(HttpStatus.TOO_MANY_REQUESTS, "Operation too frequent, please try again later."),
    SERVICE_DOWN(HttpStatus.SERVICE_UNAVAILABLE, "Service is not available at this time, please try again later."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Part of service is running into error, please contact administration for help.");



    private HttpStatus status;

    private String message;

    private ServiceRequestStatus(HttpStatus status, String message) {

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
