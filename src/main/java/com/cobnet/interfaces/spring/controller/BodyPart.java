package com.cobnet.interfaces.spring.controller;

public interface BodyPart {

    Class<?> getType();

    String getName();

    boolean isRequired();

    boolean isIsolated();

    BodyPart[] getReferenced();

    BodyPartKey[] getKeys();
}
