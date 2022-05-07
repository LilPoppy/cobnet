package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum PhoneNumberSmsVerifyResultStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.OK),
    FAILED(HttpStatus.FORBIDDEN);

    private final HttpStatus status;

    private PhoneNumberSmsVerifyResultStatus(HttpStatus status) {
        this.status = status;
    }


    @Override
    public HttpStatus getStatus() {
        return this.status;
    }
}
