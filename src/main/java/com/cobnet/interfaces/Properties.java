package com.cobnet.interfaces;

public interface Properties<T> {

    public void set(String key, T value);

    public boolean remove(String key);

}
