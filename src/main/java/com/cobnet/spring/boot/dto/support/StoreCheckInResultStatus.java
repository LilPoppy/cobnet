package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum StoreCheckInResultStatus implements ReasonableStatus {

    SUCCESS(HttpStatus.OK);

    private HttpStatus status;

    private StoreCheckInResultStatus(HttpStatus status) {

        this.status = status;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }
}
