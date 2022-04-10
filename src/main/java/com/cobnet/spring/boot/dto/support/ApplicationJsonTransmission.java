package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.Transmission;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Map;

public class ApplicationJsonTransmission implements Transmission<Map<String,Object>>, Serializable {

    @Override
    @JsonIgnore
    public Map<String, Object> getData() {

        return ProjectBeanHolder.getObjectMapper().convertValue(this, Map.class);
    }

    @Override
    public String toString() {

        return getData().toString();
    }
}
