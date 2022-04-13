package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.spring.dto.ServiceOption;
import com.cobnet.interfaces.spring.dto.ServiceOptionGenerator;

public abstract class ServiceOptionBase<T> implements ServiceOption<T> {

    private String name;

    private Class<T> type;

    public ServiceOptionBase(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Class<T> type() {
        return this.type;
    }
}
