package com.cobnet.interfaces.connection.web;

import org.springframework.http.HttpStatus;

public interface ReasonableStatus {

    public default int getCode() {

        return this.getStatus().value();
    }

    HttpStatus getStatus();

    String message();

    String name();
}
