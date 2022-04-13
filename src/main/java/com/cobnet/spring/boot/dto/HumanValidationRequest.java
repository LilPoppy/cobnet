package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.spring.boot.dto.support.HumanValidationRequestStatus;
import com.cobnet.spring.boot.dto.support.HumanValidationRequestType;

import java.util.List;
import java.util.stream.Stream;

public record HumanValidationRequest(HumanValidationRequestType type, String username, String key) implements ApplicationJson {

    @Override
    public String key() {
        return key;
    }

    @Override
    public String username() {
        return username;
    }
}
