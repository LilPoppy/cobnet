package com.storechain.interfaces.connection;

import com.storechain.connection.netty.NettySession;
import com.storechain.connection.netty.NettyServer;

import io.netty.channel.Channel;

@SuppressWarnings("rawtypes")
public interface NettySessionBuilder<T extends NettySession, E extends Channel> {
	
	T build(NettyServer server, E channel);
}
