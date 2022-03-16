package com.cobnet.event.polyglot;

import com.cobnet.polyglot.PolyglotEngine;

import java.time.Clock;

public class PolyglotEngineCloseEvent extends  PolyglotEvent<PolyglotEngine> {

    public PolyglotEngineCloseEvent(PolyglotEngine source, boolean cancelIfExecuting) {
        super(source, cancelIfExecuting);
    }

    public PolyglotEngineCloseEvent(PolyglotEngine source, Clock clock, boolean cancelIfExecuting) {
        super(source, clock, cancelIfExecuting);
    }

    public boolean isCancelIfExecuting() {

        return (boolean) this.getArguments()[0];
    }
}
