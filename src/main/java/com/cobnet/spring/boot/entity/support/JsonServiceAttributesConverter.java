package com.cobnet.spring.boot.entity.support;

import com.cobnet.interfaces.spring.dto.ServiceOption;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Converter(autoApply = true)
public class JsonServiceAttributesConverter extends JsonSetConverter<ServiceOption<?>> {

    @Override
    public Set<ServiceOption<?>> convertToEntityAttribute(String dbData) {

        try {

            return dbData != null ? ProjectBeanHolder.getObjectMapper().readValue(dbData, Set.class) : new HashSet<>();

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return null;
    }
}
