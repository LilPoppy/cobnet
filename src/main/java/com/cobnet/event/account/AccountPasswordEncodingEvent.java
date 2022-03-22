package com.cobnet.event.account;

import com.cobnet.interfaces.event.Cancellable;
import com.cobnet.interfaces.security.Account;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AccountPasswordEncodingEvent extends AccountEvent implements Cancellable {

    private String password;

    private final PasswordEncoder encoder;

    private boolean cancelled;

    public AccountPasswordEncodingEvent(Account source, String password, PasswordEncoder encoder) {

        super(source, password);

        this.password = password;
        this.encoder = encoder;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public String getPassword() {

        return this.password;
    }

    public PasswordEncoder getEncoder() {

        return this.encoder;
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
