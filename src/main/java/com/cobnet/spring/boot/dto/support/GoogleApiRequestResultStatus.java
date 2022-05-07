package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum GoogleApiRequestResultStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.OK),
    FAILED(HttpStatus.BAD_REQUEST),
    EXHAUSTED(HttpStatus.BAD_REQUEST),
    REJECTED(HttpStatus.BAD_REQUEST),

    HUMAN_VALIDATION_REQUEST(HttpStatus.BAD_REQUEST),
    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    SERVICE_DOWN(HttpStatus.BAD_GATEWAY);

    private final HttpStatus status;

    private GoogleApiRequestResultStatus(HttpStatus status) {
        this.status = status;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }
}
