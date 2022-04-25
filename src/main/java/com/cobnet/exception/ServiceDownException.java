package com.cobnet.exception;

public class ServiceDownException extends Exception{

    public ServiceDownException(Object instance) {

        super(String.format("Service Class: %s is down!", instance.getClass().getName()));
    }
}
