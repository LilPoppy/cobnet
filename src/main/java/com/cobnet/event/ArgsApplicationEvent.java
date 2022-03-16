package com.cobnet.event;

import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public abstract class ArgsApplicationEvent<T> extends ApplicationEvent {

    private final Object[] arguments;

    public ArgsApplicationEvent(T source, Object... args) {

        this(source, Clock.systemDefaultZone(), args);
    }

    public ArgsApplicationEvent(T source, Clock clock, Object... args) {

        super(source, clock);

        this.arguments = args;
    }

    @Override
    public T getSource() {

        return (T) super.getSource();
    }

    public Object[] getArguments() {

        return arguments;
    }
}
