package com.cobnet.exception.support;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.springframework.http.HttpStatus;

public enum SecurityRequestStatus implements ReasonableStatus {

    SECURITY_FORBIDDEN_SESSION(HttpStatus.FORBIDDEN, "Session is blocked for security reason, please contact administration for help."),
    SECURITY_PHONE_VERIFICATION_DEMAND(HttpStatus.BAD_REQUEST, "Phone number verification is required for this operation."),
    SECURITY_FORBIDDEN_HUMAN_VALIDATION(HttpStatus.FORBIDDEN, "Human validation is required for this operation."),
    SECURITY_MAXIMUM_MESSAGE(HttpStatus.FORBIDDEN, "Request is reach restricted visitor limit, please authenticate your session or try again later."),
    SECURITY_MAXIMUM_SESSION(HttpStatus.BAD_REQUEST, "IP address is reach restricted session limit.");

    private HttpStatus status;

    private String message;

    private SecurityRequestStatus(HttpStatus status, String message) {

        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String message() {
        return message;
    }
}
