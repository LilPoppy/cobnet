package com.cobnet.interfaces.connection.web;

import java.io.Serializable;
import java.util.Map;

public interface Form<T> extends Serializable {

    public Map<String, ?> getFields();

    public T getResult(Object... args);
}
