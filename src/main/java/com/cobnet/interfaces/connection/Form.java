package com.cobnet.interfaces.connection;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface Form extends Serializable {

    public Map<String, ?> getOptions();

    public static <T extends Form> T generate(FormGenerator<T> generator, Map<String, ?> options) {

        return generator.generate(options);
    }
}
