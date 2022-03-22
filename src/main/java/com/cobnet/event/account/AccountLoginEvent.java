package com.cobnet.event.account;

import com.cobnet.interfaces.event.Cancellable;
import com.cobnet.interfaces.security.Account;

public class AccountLoginEvent extends AccountEvent implements Cancellable {

    private boolean cancelled;

    public AccountLoginEvent(Account source) {

        super(source);
    }

    @Override
    public boolean isCancelled() {

        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {

        this.cancelled = cancel;
    }
}
