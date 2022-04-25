package com.cobnet.spring.boot.dto;

import com.cobnet.common.KeyValuePair;
import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.spring.boot.dto.support.UIType;

import java.util.List;

public record CheckInFormField(int groupId, String key, String value, String label, UIType type, List<KeyValuePair<String, String>> list) implements ApplicationJson {

    @Override
    public int groupId() { return groupId; }

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
    public List<KeyValuePair<String, String>> list() {
        return list;
    }
}
