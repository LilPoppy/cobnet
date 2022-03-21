package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.Packet;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Map;

public class MappedPacket implements Packet<Map<String,Object>>, Serializable {

    @Override
    @JsonIgnore
    public Map<String, Object> getRaw() {

        return ProjectBeanHolder.getObjectMapper().convertValue(this, Map.class);
    }
}
