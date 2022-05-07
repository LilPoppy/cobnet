package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum AuthenticationStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.OK),
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED),
    PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED),
    HUMAN_VALIDATION_REQUEST(HttpStatus.BAD_REQUEST),
    REACHED_MAXIMUM_ATTEMPT(HttpStatus.BAD_REQUEST),
    LOCKED(HttpStatus.BAD_REQUEST),
    REJECTED(HttpStatus.BAD_REQUEST);

    private final HttpStatus status;

    private AuthenticationStatus(HttpStatus status) {

        this.status = status;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }
}
