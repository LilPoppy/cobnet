package com.cobnet.interfaces.spring.dto;

public interface ServiceOptionGenerator<T extends ServiceOption<E>, E> {

    T generate(String name);
}
