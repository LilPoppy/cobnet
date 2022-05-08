package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum AuthenticationStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.OK, null),
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, null),
    HUMAN_VALIDATION_REQUIRED(HttpStatus.FORBIDDEN, null),
    PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, null),
    REACHED_MAXIMUM_ATTEMPT(HttpStatus.BAD_REQUEST, null),
    LOCKED(HttpStatus.BAD_REQUEST, null),
    REJECTED(HttpStatus.BAD_REQUEST, null);

    private final HttpStatus status;

    private final String message;

    private AuthenticationStatus(HttpStatus status, String message) {

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
