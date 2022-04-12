package com.cobnet.spring.boot.dto;

import com.cobnet.common.KeyValuePair;
import com.cobnet.interfaces.connection.Transmission;
import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Map;

public record OAuth2Registration(String name, String url) implements ApplicationJson {

    @Override
    public String name() {
        return name;
    }

    @Override
    public String url() {
        return url;
    }
}
