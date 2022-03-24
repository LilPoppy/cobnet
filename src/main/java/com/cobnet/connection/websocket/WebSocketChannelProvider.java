package com.cobnet.connection.websocket;

import com.cobnet.interfaces.connection.ChannelProvider;
import io.netty.channel.Channel;

public class WebSocketChannelProvider implements ChannelProvider<WebSocketServer, WebSocketChannel> {

    @Override
    public WebSocketChannel provide(WebSocketServer server, Channel channel) {

        return new WebSocketChannel(server, channel);
    }
}
