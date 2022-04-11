package com.cobnet.interfaces.connection.web;

import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public interface ApplicationJson extends Content<Map<String, Object>> {

    @JsonIgnore
    @Override
    public default Map<String, Object> getData() {

        return ProjectBeanHolder.getObjectMapper().convertValue(this, Map.class);
    }
}
