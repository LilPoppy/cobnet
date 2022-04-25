package com.cobnet.exception;

public class ServiceDownException extends Exception{

    public ServiceDownException(Class<?> clazz, Object instance) {

        super(String.format("Service Class: %s , not set %s", clazz.getName(), instance.getClass()));
    }
}
