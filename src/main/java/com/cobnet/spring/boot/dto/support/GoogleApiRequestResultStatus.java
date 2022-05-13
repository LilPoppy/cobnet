package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum GoogleApiRequestResultStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.OK, null),
    EMPTY(HttpStatus.NO_CONTENT, "No result present."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Arguments is not match the requires");

    private final HttpStatus status;

    private final String message;

    private GoogleApiRequestResultStatus(HttpStatus status, String message) {
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
