package com.cobnet.exception.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum GeneralDataStatus implements ReasonableStatus {

    NO_EXIST(HttpStatus.NOT_FOUND, "No entity present."),
    EMPTY(HttpStatus.NO_CONTENT, "Can't find any result match the requires.");

    private HttpStatus status;

    private final String message;

    GeneralDataStatus(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }

    @Override
    public String message() {
        return this.message;
    }
}
