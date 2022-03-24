package com.cobnet.connection.websocket;

import com.cobnet.common.KeyValuePair;
import com.cobnet.connection.support.NettyServer;
import com.cobnet.connection.websocket.handler.*;
import io.netty.channel.ChannelOption;

public class WebSocketServer extends NettyServer<WebSocketChannel> {

    private int maxQueueCount = 1024;

    public WebSocketServer(String name) {
        super(name);
    }

    @Override
    protected Builder build(Builder builder) {

        return builder
                .setHandler(new WebSocketServerInitializerHandler(this))
                .setChildHandler(new WebSocketChannelInitializerHandler(this))
                .setOptions(new KeyValuePair<>(ChannelOption.SO_BACKLOG, this.maxQueueCount))
                .setActivityHandler(new WebSocketActivityHandler(this))
                .setEncoder(WebSocketPacketEncodeHandler::new)
                .setInboundHandler(new WebSocketPacketInboundHandler())
                .setDecoder(WebSocketPacketDecodeHandler::new);
    }
}
