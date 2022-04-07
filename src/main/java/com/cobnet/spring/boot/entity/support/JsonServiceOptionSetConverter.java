package com.cobnet.spring.boot.entity.support;

import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.interfaces.spring.dto.ServiceOption;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.persistence.Converter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Converter(autoApply = true)
public class JsonServiceOptionSetConverter extends JsonSetConverter<ServiceOption> {


    @Override
    public Set<ServiceOption> convertToEntityAttribute(String dbData) {

        try {

            return dbData != null ? ProjectBeanHolder.getObjectMapper().readValue(dbData, new TypeReference<Set<ServiceOption>>() {}) : new HashSet<>();

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return null;
    }
}
