package com.storechain.interfaces.connection;

import com.storechain.connection.netty.NettyNioSocketChannel;
import com.storechain.connection.netty.NettyServer;

import io.netty.channel.Channel;

public interface NettyChannelBuilder<T extends Channel, E extends NettyNioSocketChannel<?>> {
	
	E build(NettyServer server, T channel);
}
