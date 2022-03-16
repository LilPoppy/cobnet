package com.cobnet.event.polyglot;

import com.cobnet.event.ArgsApplicationEvent;

import java.time.Clock;

public class PolyglotEvent<T> extends ArgsApplicationEvent<T> {

    public PolyglotEvent(T source, Object... args) {
        super(source, args);
    }

    public PolyglotEvent(T source, Clock clock, Object... args) {
        super(source, clock, args);
    }
}
