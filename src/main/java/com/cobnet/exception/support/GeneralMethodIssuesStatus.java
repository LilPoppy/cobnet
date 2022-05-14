package com.cobnet.exception.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum GeneralMethodIssuesStatus implements ReasonableStatus {

    UNACCEPTABLE_PARAMETER(HttpStatus.BAD_REQUEST, "Please double check the parameters.");

    private HttpStatus status;

    private final String message;

    GeneralMethodIssuesStatus(HttpStatus status, String message) {
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
