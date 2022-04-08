package com.cobnet.spring.boot.entity.support;

import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.Staff;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.persistence.Converter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Converter(autoApply = true)
public class JsonStaffSetConverter extends JsonSetConverter<Staff> {

    @Override
    public Set<Staff> convertToEntityAttribute(String dbData) {

        try {
            return dbData != null ? ProjectBeanHolder.getObjectMapper().readValue(dbData, HashSet.class) : new HashSet<>();

        } catch (JsonProcessingException e) {

            e.printStackTrace();
        }
        return null;
    }
}
