package com.cobnet.connection.support;

import com.cobnet.interfaces.connection.AuthenticatableChannel;
import com.cobnet.interfaces.security.Account;
import io.netty.channel.Channel;

public class AuthenticatedChannel extends NettyChannel implements AuthenticatableChannel {

    private Account account;

    protected AuthenticatedChannel(NettyServer<? extends NettyChannel> server, Channel channel, Account account) {

        super(server, channel);
        this.account = account;
    }

    @Override
    public Account getAccount() {

        return this.account;
    }

    @Override
    public void setAccount(Account account) {

        this.account = account;
    }

    @Override
    public boolean isAuthenticated() {

        return this.getAccount() != null;
    }
}
