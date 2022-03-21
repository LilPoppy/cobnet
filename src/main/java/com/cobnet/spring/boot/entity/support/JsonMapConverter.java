package com.cobnet.spring.boot.entity.support;

import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Converter(autoApply = true)
public class JsonMapConverter implements AttributeConverter<Map<String,Object>, String> {

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {

        try {

            return ProjectBeanHolder.getObjectMapper().writeValueAsString(attribute);

        } catch (JsonProcessingException ex) {

            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {

        try {

            return dbData != null ? ProjectBeanHolder.getObjectMapper().readValue(dbData, HashMap.class) : new HashMap<>();

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return null;
    }
}
