package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.Form;
import com.cobnet.interfaces.connection.FormGenerator;
import com.cobnet.spring.boot.dto.support.HttpMapTransmission;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public abstract class FormBase<T extends Form, E> extends HttpMapTransmission implements Form {

    @JsonIgnore
    public abstract FormGenerator<T> getGenerator();

    @JsonIgnore
    public abstract E getEntity();

    @Override
    @JsonIgnore
    public Map<String, ?> getOptions() {

        return this.getData();
    }

}
