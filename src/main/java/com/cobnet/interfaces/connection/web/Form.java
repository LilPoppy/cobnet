package com.cobnet.interfaces.connection.web;

import java.io.Serializable;
import java.util.Map;

public interface Form extends Serializable {

    public Map<String, ?> getFields();

    public static <T extends Form> T generate(FormGenerator<T> generator, Map<String, ?> options) {

        return generator.generate(options);
    }
}
