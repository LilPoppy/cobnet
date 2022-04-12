package com.cobnet.interfaces.connection.web;

import com.cobnet.interfaces.connection.web.Form;

import java.util.Map;

public interface FormGenerator<T extends Form> {

    public T generate(Map<String, ?> fields);
}
