package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.interfaces.connection.web.Form;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public abstract class FormBase<T extends Form<E>, E> implements Form<E>, ApplicationJson {

    @Override
    @JsonIgnore
    public Map<String, ?> getFields() {

        return this.getData();
    }

}
