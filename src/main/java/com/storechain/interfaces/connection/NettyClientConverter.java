package com.storechain.interfaces.connection;

import com.storechain.connection.netty.NettyClient;
import com.storechain.connection.netty.NettyServer;

import io.netty.channel.Channel;

@SuppressWarnings("rawtypes")
public interface NettyClientConverter<T extends NettyClient, E extends Channel> {
	
	T transform(NettyServer server, E channel);
}
