package com.cobnet.common;

import java.util.function.Function;

public class Delegate<T> {

    private T delegator;

    public Delegate(T delegator) {

        this.delegator = delegator;
    }

    public <E> E invoke(Function<T, E> function) {

        return function.apply(this.delegator);
    }
}
