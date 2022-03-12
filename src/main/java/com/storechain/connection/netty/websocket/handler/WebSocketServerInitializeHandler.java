package com.storechain.connection.netty.websocket.handler;


import javax.net.ssl.SSLHandshakeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.storechain.connection.handler.AuthenticationPacketHandler;
import com.storechain.connection.handler.UnknownPacketHandler;
import com.storechain.connection.netty.websocket.WebSocketServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.logging.LoggingHandler;

public class WebSocketServerInitializeHandler extends LoggingHandler {
	
	protected static Logger log = LoggerFactory.getLogger(WebSocketServerInitializeHandler.class);
	
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    	
    	super.channelRegistered(ctx);

		WebSocketServer server = (WebSocketServer) ctx.channel().attr(WebSocketServer.SERVER_KEY).get();

	    server.addListener(new UnknownPacketHandler(server));
	    server.addListener(new AuthenticationPacketHandler(server));
    }
    
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		
		ctx.close();

		if(cause instanceof SSLHandshakeException) {
			log.warn(String.format("%s has initialize without successed.", ctx.channel().remoteAddress()));
			return;
		}
		
		super.exceptionCaught(ctx, cause);
		
	}
}
