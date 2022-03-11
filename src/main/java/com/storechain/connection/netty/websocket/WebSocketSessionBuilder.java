package com.storechain.connection.netty.websocket;

import com.storechain.connection.netty.NettyServer;
import com.storechain.interfaces.connection.NettySessionBuilder;

import io.netty.channel.socket.nio.NioSocketChannel;

public class WebSocketSessionBuilder implements NettySessionBuilder<WebSocketSession, NioSocketChannel> {

	@Override
	public WebSocketSession build(NettyServer server, NioSocketChannel channel) {
		return new WebSocketSession((WebSocketServer) server, channel);
	}

}
