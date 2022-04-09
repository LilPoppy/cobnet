package com.cobnet.interfaces.connection;

import java.util.Map;
import java.util.Set;

public interface FormGenerator<T extends Form> {

    public T generate(Map<String, ?> options);
}
