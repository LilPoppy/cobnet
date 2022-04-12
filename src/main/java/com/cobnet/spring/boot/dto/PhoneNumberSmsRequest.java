package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsType;

public record PhoneNumberSmsRequest(String username, String phoneNumber, PhoneNumberSmsType type) implements ApplicationJson {

    @Override
    public String username() {
        return username;
    }

    @Override
    public String phoneNumber() {
        return phoneNumber;
    }

    @Override
    public PhoneNumberSmsType type() { return type; }

}
