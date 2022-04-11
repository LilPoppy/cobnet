package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsRequestType;

public record PhoneNumberSmsRequest(String username, String phoneNumber, PhoneNumberSmsRequestType type) implements ApplicationJson {

    @Override
    public String username() {
        return username;
    }

    @Override
    public String phoneNumber() {
        return phoneNumber;
    }

    @Override
    public PhoneNumberSmsRequestType type() { return type; }

}
