package com.cobnet.exception;

import com.cobnet.interfaces.connection.web.Content;
import com.cobnet.interfaces.connection.web.ReasonableStatus;

import java.util.Collection;

public class ResponseFailureStatusException extends Exception {

    private ReasonableStatus status;

    private Content<?>[] params;

    public <T extends Enum & ReasonableStatus> ResponseFailureStatusException(T status, Content<?>... params) {

        this.status = status;
        this.params = params;
    }

    public <T extends Enum & ReasonableStatus> ResponseFailureStatusException(T status, Collection<? extends Content<?>> params) {

        this(status, params.stream().toArray(Content[]::new));
    }

    public <T extends Enum & ReasonableStatus> T getStatus() {

        return (T) status;
    }

    public Content<?>[] getParams() {
        return params;
    }
}
