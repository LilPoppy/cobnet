package com.cobnet.interfaces.spring.dto;

public interface ServiceOptionGenerator<T extends ServiceOption> {

    T generate(String name);
}
