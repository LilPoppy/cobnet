package com.storechain.connection.netty;

import com.storechain.connection.InboundPacket;
import com.storechain.interfaces.connection.NettyClientConverter;

import io.netty.buffer.Unpooled;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyBaseClientConverter implements NettyClientConverter<NettyClient, NioSocketChannel> {

	@Override
	public NettyClient transform(NettyServer server, NioSocketChannel channel) {
		return new NettyClient(server, channel, new InboundPacket(Unpooled.buffer(), server.getConfiguration().getDecodeEndian(), server.getConfiguration().getCharset()));
	}

}
