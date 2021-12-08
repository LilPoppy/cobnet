package com.storechain.connection.netty.websocket;

import com.storechain.connection.netty.NettyServer;
import com.storechain.interfaces.connection.NettyClientConverter;

import io.netty.channel.socket.nio.NioSocketChannel;

public class WebSocketClientConverter implements NettyClientConverter<WebSocketClient, NioSocketChannel> {

	@Override
	public WebSocketClient transform(NettyServer server, NioSocketChannel channel) {
		return new WebSocketClient((WebSocketServer) server, channel);
	}

}
