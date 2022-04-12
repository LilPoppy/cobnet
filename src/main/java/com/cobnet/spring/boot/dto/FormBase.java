package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.interfaces.connection.web.Form;
import com.cobnet.interfaces.connection.web.FormGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public abstract class FormBase<T extends Form, E> implements Form, ApplicationJson {

    @JsonIgnore
    public abstract FormGenerator<T> getGenerator();

    @JsonIgnore
    public abstract E getEntity();

    @Override
    @JsonIgnore
    public Map<String, ?> getFields() {

        return this.getData();
    }

}