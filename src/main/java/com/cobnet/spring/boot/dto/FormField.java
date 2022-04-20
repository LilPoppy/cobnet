package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.spring.boot.dto.support.UIType;

public record FormField(String key, String value, String label, UIType type, Object addition) implements ApplicationJson {

    @Override
    public String key() {
        return key;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public UIType type() {
        return type;
    }

    @Override
    public Object addition() {
        return addition;
    }
}
