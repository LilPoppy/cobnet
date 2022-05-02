package com.cobnet.exception;

import com.cobnet.interfaces.connection.web.ReasonableStatus;

public class ResponseFailureStatusException extends Exception {

    private ReasonableStatus status;

    public <T extends Enum & ReasonableStatus> ResponseFailureStatusException(T status) {

        this.status = status;
    }

    public ReasonableStatus getStatus() {

        return status;
    }
}
