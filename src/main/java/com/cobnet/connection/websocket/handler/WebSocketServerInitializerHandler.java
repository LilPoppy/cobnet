package com.cobnet.connection.websocket.handler;

import com.cobnet.connection.support.handler.ServerInitializerHandler;
import com.cobnet.connection.websocket.WebSocketChannel;
import com.cobnet.connection.websocket.WebSocketServer;

public class WebSocketServerInitializerHandler extends ServerInitializerHandler<WebSocketServer, WebSocketChannel> {

    public WebSocketServerInitializerHandler(WebSocketServer server) {
        super(server);
    }

}
