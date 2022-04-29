package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.PageFieldProperties;

import java.util.Map;
import java.util.Set;

public class DynamicPageFieldProperties implements PageFieldProperties {

    private Map<String, Object> map;

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
