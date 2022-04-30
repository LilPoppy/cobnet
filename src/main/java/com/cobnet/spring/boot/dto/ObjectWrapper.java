package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;

public final class ObjectWrapper<T> implements ApplicationJson {

    private final String name;

    private T value;

    public ObjectWrapper() {
        this.name = "value";
    }

    public ObjectWrapper(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ObjectWrapper{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
