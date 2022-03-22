package com.cobnet.event.account;

import com.cobnet.event.ArgsApplicationEvent;
import com.cobnet.interfaces.security.Account;

public class AccountEvent extends ArgsApplicationEvent<Account> {

    public AccountEvent(Account source, Object... args) {
        super(source, args);
    }
}
