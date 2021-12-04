package com.storechain.connection.handler;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLHandshakeException;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslHandler;

public class WebSocketSslHandler extends SslHandler {
	
	protected static Logger log = Logger.getLogger(WebSocketSslHandler.class);

	public WebSocketSslHandler(SSLEngine engine) {
		super(engine);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		
		ctx.close();

		if(cause instanceof SSLHandshakeException) {
			log.warn(String.format("%s has access without ssl certificate.", ctx.channel().remoteAddress()));
			return;
		}
		
		super.exceptionCaught(ctx, cause);
		
	}

}
