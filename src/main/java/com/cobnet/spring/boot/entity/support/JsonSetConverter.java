package com.cobnet.spring.boot.entity.support;

import com.cobnet.interfaces.security.Permission;
import com.cobnet.security.permission.UserPermission;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public abstract class JsonSetConverter<T> implements AttributeConverter<Set<T>, String> {

    @Override
    public String convertToDatabaseColumn(Set<T> attribute) {

        try {
            return ProjectBeanHolder.getObjectMapper().writeValueAsString(attribute);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
