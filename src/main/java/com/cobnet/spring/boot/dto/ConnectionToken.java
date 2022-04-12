package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;

import java.util.Date;
import java.util.UUID;

public record ConnectionToken(Date initialTime, String token) implements ApplicationJson {

    public static final String ATTRIBUTE_KEY = "CONNECTION_TOKEN";

    @Override
    public Date initialTime() {
        return initialTime;
    }

    @Override
    public String token() {
        return token;
    }

    public static ConnectionToken generate() {

        return new ConnectionToken(new Date(System.currentTimeMillis()), UUID.randomUUID().toString());
    }
}
