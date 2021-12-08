package com.storechain.connection.netty.websocket.handler;


import com.storechain.connection.handler.UnknownPacketHandler;
import com.storechain.connection.netty.websocket.WebSocketServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.logging.LoggingHandler;

public class WebSocketServerInitializeHandler extends LoggingHandler {
	
	
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    	super.channelRegistered(ctx);
    	
		WebSocketServer server = (WebSocketServer) ctx.channel().attr(WebSocketServer.SERVER_KEY).get();
		
	    server.addListener(new UnknownPacketHandler(server));
    }
}
