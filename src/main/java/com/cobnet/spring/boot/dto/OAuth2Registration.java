package com.cobnet.spring.boot.dto;

import com.cobnet.common.KeyValuePair;
import com.cobnet.interfaces.connection.Transmission;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Map;

public class OAuth2Registration implements Transmission<Map.Entry<String, String>>, Serializable {

    private String name;

    private String url;

    public OAuth2Registration(String name, String url) {

        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    @JsonIgnore
    public Map.Entry<String, String> getData() {
        return new KeyValuePair<>(this.name, this.url);
    }
}
