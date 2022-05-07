package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum StoreCheckInPageDetailResultStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.OK),
    NO_EXIST(HttpStatus.NOT_FOUND),
    SERVICE_DOWN(HttpStatus.SERVICE_UNAVAILABLE);

    private final HttpStatus status;

    private StoreCheckInPageDetailResultStatus(HttpStatus status) {
        this.status = status;
    }


    @Override
    public HttpStatus getStatus() {
        return this.status;
    }
}

