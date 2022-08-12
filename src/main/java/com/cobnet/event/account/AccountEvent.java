package com.cobnet.event.account;

import com.cobnet.event.ArgsApplicationEvent;
import com.cobnet.interfaces.security.Account;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class AccountEvent extends ArgsApplicationEvent<Account> {

    public AccountEvent(Account source, Object... args) {
        super(source, args);
        List<Date> date = new ArrayList<>();
        date.sort(Comparator.naturalOrder());
    }
}
