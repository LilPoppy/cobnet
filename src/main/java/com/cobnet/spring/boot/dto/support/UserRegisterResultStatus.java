package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum UserRegisterResultStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.CREATED),
    UNACCEPTABLE_CONTENT(HttpStatus.BAD_REQUEST),
    VERIFICATION_FAILED(HttpStatus.REQUEST_TIMEOUT),
    REJECTED(HttpStatus.BAD_REQUEST);

    private final HttpStatus status;

    private UserRegisterResultStatus(HttpStatus status) {
        this.status = status;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }
}
