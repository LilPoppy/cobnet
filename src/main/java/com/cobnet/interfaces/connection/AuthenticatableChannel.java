package com.cobnet.interfaces.connection;

import com.cobnet.interfaces.security.Account;
import io.netty.channel.Channel;
//TODO refresh token after logout
public interface AuthenticatableChannel extends Channel {

    public Account getAccount();

    public void setAccount(Account account);

    public boolean isAuthenticated();
}
