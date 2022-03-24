package com.cobnet.connection.websocket;

import com.cobnet.connection.support.NettyChannel;
import com.cobnet.connection.support.NettyServer;
import io.netty.channel.Channel;

public class WebSocketChannel extends NettyChannel {

    public WebSocketChannel(NettyServer<? extends NettyChannel> server, Channel channel) {
        super(server, channel);
    }
}
