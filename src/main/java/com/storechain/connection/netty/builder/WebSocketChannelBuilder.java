package com.storechain.connection.netty.builder;

import com.storechain.connection.netty.NettyServer;
import com.storechain.connection.netty.websocket.WebSocketChannel;
import com.storechain.connection.netty.websocket.WebSocketServer;
import com.storechain.interfaces.connection.NettyChannelBuilder;

import io.netty.channel.socket.nio.NioSocketChannel;

public class WebSocketChannelBuilder implements NettyChannelBuilder<NioSocketChannel, WebSocketChannel> {

	@Override
	public WebSocketChannel build(NettyServer server, NioSocketChannel channel) {
		
		return new WebSocketChannel((WebSocketServer) server, channel);
	}
}
