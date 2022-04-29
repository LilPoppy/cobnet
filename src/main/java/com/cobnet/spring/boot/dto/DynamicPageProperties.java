package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.PageProperties;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DynamicPageProperties implements PageProperties {

    private Map<String, Object> map;

    public DynamicPageProperties(Map.Entry<String, Object>... properties) {

        this.map = Arrays.stream(properties).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Object set(String key, Object value) {

        return map.put(key, value);
    }

    @Override
    public Object remove(String key) {

        return map.remove(key);
    }

    @Override
    public Set<Map.Entry<String, Object>> properties() {
        return map.entrySet();
    }
}
