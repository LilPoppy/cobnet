package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum UserRegisterResultStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.CREATED, null),
    UNACCEPTABLE_CONTENT(HttpStatus.BAD_REQUEST, "Please check the form your have summit is not match requirement."),
    VERIFICATION_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "Phone number verification is expired, please try to verify your phone number again.");

    private final HttpStatus status;

    private final String message;

    private UserRegisterResultStatus(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }

    @Override
    public String message() {
        return null;
    }
}
