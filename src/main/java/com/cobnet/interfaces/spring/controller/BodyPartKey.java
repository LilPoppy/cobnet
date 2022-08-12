package com.cobnet.interfaces.spring.controller;

import java.lang.reflect.Type;

public interface BodyPartKey {

    Type getType();

    void setType(Type type);

    String getKey();

    Object getValue();

    void setValue(Object value);

    String[] getReferences();

    boolean isNullable();

    BodyPart getBodyPart();
}
