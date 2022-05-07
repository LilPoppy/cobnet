package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum StoreRegisterResultStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.CREATED),
    STORE_NONEXISTENT(HttpStatus.BAD_REQUEST),
    EXHAUSTED(HttpStatus.BAD_REQUEST),
    STORE_PERMANENTLY_CLOSED(HttpStatus.BAD_REQUEST),
    STORE_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST),
    SECURITY_CHECK(HttpStatus.FORBIDDEN),
    HUMAN_VALIDATION_REQUEST(HttpStatus.BAD_REQUEST),
    SERVICE_DOWN(HttpStatus.SERVICE_UNAVAILABLE);

    private final HttpStatus status;

    private StoreRegisterResultStatus(HttpStatus status) {
        this.status = status;
    }


    @Override
    public HttpStatus getStatus() {
        return this.status;
    }
}
