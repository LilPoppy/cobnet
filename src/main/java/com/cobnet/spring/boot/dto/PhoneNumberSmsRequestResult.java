package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsRequestResultStatus;

public record PhoneNumberSmsRequestResult(PhoneNumberSmsRequestResultStatus status) implements ApplicationJson {

    public PhoneNumberSmsRequestResultStatus status() {
        return status;
    }
}
