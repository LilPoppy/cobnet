package com.cobnet.spring.boot.entity.support;

import com.cobnet.security.permission.UserPermission;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.persistence.Converter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Converter(autoApply = true)
public class JsonPermissionSetConverter extends JsonSetConverter<UserPermission> {


    @Override
    public Set<UserPermission> convertToEntityAttribute(String dbData) {

        try {

            return dbData != null ? ProjectBeanHolder.getObjectMapper().readValue(dbData, new TypeReference<Set<UserPermission>>() {}) : new HashSet<>();

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return null;
    }
}
