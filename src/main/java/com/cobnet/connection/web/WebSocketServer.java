package com.cobnet.connection.web;

import com.cobnet.connection.InboundPacket;
import com.cobnet.connection.AuthenticatedChannel;
import com.cobnet.connection.NettyServer;

public class WebSocketServer extends NettyServer<AuthenticatedChannel> {

    public WebSocketServer(String name) {
        super(name);
    }

    @Override
    protected Builder builder(Builder builder) {

        return builder.setAttributes();
    }
}
