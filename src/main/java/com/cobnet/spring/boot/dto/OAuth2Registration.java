package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;

public record OAuth2Registration(String name, String url, Base64File icon) implements ApplicationJson {

    @Override
    public String name() {
        return name;
    }

    @Override
    public String url() {
        return url;
    }
}
