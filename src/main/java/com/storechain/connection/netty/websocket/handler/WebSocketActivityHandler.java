package com.storechain.connection.netty.websocket.handler;

import com.storechain.connection.netty.handler.InboundHandler;
import com.storechain.connection.netty.websocket.WebSocketServer;

import io.netty.channel.ChannelHandlerContext;

public class WebSocketActivityHandler extends InboundHandler {

	private final WebSocketServer server;

	public WebSocketActivityHandler(WebSocketServer server) {
		super();
		this.server = server;
	}


	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

		if (server.getConfiguration().isLogUserActive()) {
			log.info(String.format("%s has connected to websocket.", ctx.channel().remoteAddress()));
		}

		server.add(ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

		if (server.getConfiguration().isLogUserInactive()) {
			log.info(String.format("%s has disconnected from websocket.", ctx.channel().remoteAddress()));
		}

		server.remove(ctx.channel());
	}
}
