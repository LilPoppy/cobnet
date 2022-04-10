package com.cobnet.spring.boot.dto;

import com.cobnet.spring.boot.dto.support.ApplicationJsonTransmission;

import java.util.Date;
import java.util.UUID;

public class ConnectionToken extends ApplicationJsonTransmission {

    public static final String ATTRIBUTE_KEY = "CONNECTION_TOKEN";

    private final Date initialTime;

    private String token;

    public ConnectionToken(Date initialTime, String token) {
        this.initialTime = initialTime;
        this.token = token;
    }

    public Date getInitialTime() {

        return initialTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static ConnectionToken generate() {

        return new ConnectionToken(new Date(System.currentTimeMillis()), UUID.randomUUID().toString());
    }
}
