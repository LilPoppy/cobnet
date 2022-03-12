package com.storechain.connection.netty.builder;

import com.storechain.connection.InboundPacket;
import com.storechain.connection.netty.NettyNioSocketChannel;
import com.storechain.connection.netty.NettyServer;
import com.storechain.interfaces.connection.NettyChannelBuilder;

import io.netty.buffer.Unpooled;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyBaseChannelBuilder implements NettyChannelBuilder<NioSocketChannel, NettyNioSocketChannel<?>> {

	@Override
	public NettyNioSocketChannel<?> build(NettyServer server, NioSocketChannel channel) {
		
		return new NettyNioSocketChannel<InboundPacket>(server, channel, new InboundPacket(Unpooled.buffer(), server.getConfiguration().getDecodeEndian(), server.getConfiguration().getCharset()));
	}


}
