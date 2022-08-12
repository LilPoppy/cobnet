package com.cobnet.spring.boot.controller.support;

import org.springframework.core.MethodParameter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ReferencableMethodParameter extends MethodParameter {

    private final String name;
    private MethodParameter[] referenced;

    public ReferencableMethodParameter(String name, Method method, int parameterIndex) {
        super(method, parameterIndex);
        this.name = name;
    }

    public ReferencableMethodParameter(String name, Method method, int parameterIndex, int nestingLevel) {
        super(method, parameterIndex, nestingLevel);
        this.name = name;
    }

    public ReferencableMethodParameter(String name, Constructor<?> constructor, int parameterIndex) {
        super(constructor, parameterIndex);
        this.name = name;
    }

    public ReferencableMethodParameter(String name, Constructor<?> constructor, int parameterIndex, int nestingLevel) {
        super(constructor, parameterIndex, nestingLevel);
        this.name = name;
    }

    public ReferencableMethodParameter(String name, MethodParameter original) {
        super(original);
        this.name = name;
    }

    public MethodParameter[] getReferenced() {
        return referenced;
    }

    public String getName() {
        return name;
    }

    public void setReferenced(MethodParameter... referenced) {
        this.referenced = referenced;
    }
}
