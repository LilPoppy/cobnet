package com.cobnet.event.polyglot;

import com.cobnet.event.ArgsApplicationEvent;
import com.cobnet.polyglot.PolyglotContext;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class PolyglotContextCloseEvent extends PolyglotEvent<PolyglotContext> {

    public PolyglotContextCloseEvent(PolyglotContext source, boolean cancelIfExecuting) {
        super(source, cancelIfExecuting);
    }

    public PolyglotContextCloseEvent(PolyglotContext source, Clock clock, boolean cancelIfExecuting) {
        super(source, clock, cancelIfExecuting);
    }

    public boolean isCancelIfExecuting() {

        return (boolean) this.getArguments()[0];
    }
}
