package com.storechain.connection.netty;

import com.storechain.connection.InboundPacket;
import com.storechain.interfaces.connection.NettySessionBuilder;

import io.netty.buffer.Unpooled;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyBaseClientConverter implements NettySessionBuilder<NettySession, NioSocketChannel> {

	@Override
	public NettySession build(NettyServer server, NioSocketChannel channel) {
		return new NettySession(server, channel, new InboundPacket(Unpooled.buffer(), server.getConfiguration().getDecodeEndian(), server.getConfiguration().getCharset()));
	}

}
