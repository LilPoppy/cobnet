package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.spring.boot.dto.support.PhoneNumberSmsType;

public record PhoneNumberSmsVerify(String username, PhoneNumberSmsType type, int code) implements ApplicationJson {

    @Override
    public String username() {
        return username;
    }

    @Override
    public PhoneNumberSmsType type() {
        return type;
    }

    @Override
    public int code() {
        return code;
    }
}
