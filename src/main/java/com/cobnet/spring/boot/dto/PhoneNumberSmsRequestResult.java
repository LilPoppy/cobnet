package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsRequestResultStatus;

public record PhoneNumberSmsRequestResult(PhoneNumberSmsRequestResultStatus status, Object result) implements ApplicationJson {

    public PhoneNumberSmsRequestResult(PhoneNumberSmsRequestResultStatus status) {

        this(status, null);
    }

    public PhoneNumberSmsRequestResultStatus status() {
        return status;
    }

    @Override
    public Object result() {
        return result;
    }
}
