package com.cobnet.interfaces;

import com.cobnet.common.KeyValuePair;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Properties<T> {

    public T set(String key, T value);

    public T remove(String key);

    Set<Map.Entry<String, T>> properties();

}
