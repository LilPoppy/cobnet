package com.cobnet.spring.boot.dto;

import java.util.Date;

public class ConnectionToken extends MappedPacket {

    private final Date initialTime;

    private String token;

    private String address;

    private int port;

    public ConnectionToken(Date initialTime, String token, String address, int port) {
        this.initialTime = initialTime;
        this.token = token;
        this.address = address;
        this.port = port;
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

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
