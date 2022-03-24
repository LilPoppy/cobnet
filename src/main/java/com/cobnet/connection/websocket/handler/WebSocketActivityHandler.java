package com.cobnet.connection.websocket.handler;

import com.cobnet.connection.support.handler.ChannelActivityHandler;
import com.cobnet.connection.websocket.WebSocketChannel;
import com.cobnet.connection.websocket.WebSocketServer;

public class WebSocketActivityHandler extends ChannelActivityHandler<WebSocketServer, WebSocketChannel> {

    public WebSocketActivityHandler(WebSocketServer server) {

        super(server);
    }

    @Override
    protected void channelActive(WebSocketChannel channel) {

    }

    @Override
    protected void channelInactive(WebSocketChannel channel) {

    }
}
