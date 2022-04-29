package com.cobnet.interfaces;

import com.cobnet.common.KeyValuePair;
import net.minidev.json.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Properties<T> extends Serializable {

    @JsonIgnore
    public T set(String key, T value);

    @JsonIgnore
    public T remove(String key);

    Set<Map.Entry<String, T>> properties();

}
