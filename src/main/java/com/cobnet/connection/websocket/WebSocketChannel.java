package com.cobnet.connection.websocket;

import com.cobnet.connection.support.NettyChannel;
import com.cobnet.connection.support.NettyServer;
import com.cobnet.interfaces.connection.AuthenticatableChannel;
import com.cobnet.interfaces.security.Account;
import io.netty.channel.Channel;

public class WebSocketChannel extends NettyChannel implements AuthenticatableChannel {

    private Account account;

    public WebSocketChannel(NettyServer<? extends NettyChannel> server, Channel channel) {
        super(server, channel);
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

        return this.account != null;
    }
}
