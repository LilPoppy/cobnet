package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsRequestResultReason;

public record PhoneNumberSmsRequestResult(PhoneNumberSmsRequestResultReason result) implements ApplicationJson {

    @Override
    public PhoneNumberSmsRequestResultReason result() {
        return result;
    }
}
