package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.FormGenerator;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.Position;

import java.util.Map;

public class PositionRegisterForm extends FormBase<PositionRegisterForm, Position> {

    private String name;

    private boolean isDefault;

    @Override
    public FormGenerator<PositionRegisterForm> getGenerator() {

        return new PositionRegisterFormGenerator();
    }

    @Override
    public Position getEntity() {

        return new Position(name, isDefault);
    }

    public String getName() {
        return name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public static class PositionRegisterFormGenerator implements FormGenerator<PositionRegisterForm> {

        @Override
        public PositionRegisterForm generate(Map<String, ?> fields) {

             return ProjectBeanHolder.getObjectMapper().convertValue(fields, PositionRegisterForm.class);
        }
    }
}
