package com.storechain.connection.netty;

import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.storechain.connection.netty.handler.ChannelInitializeHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyConnectionAdapter {

	protected static Logger log = LogManager.getLogger(NettyConnectionAdapter.class);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ChannelFuture connect(String ip, int port, ChannelInitializeHandler handler,
			Entry<ChannelOption, Object>... options) throws InterruptedException {

		EventLoopGroup group = new NioEventLoopGroup();

		Bootstrap b = new Bootstrap();
		b.group(group);
		b.channel(NioSocketChannel.class);

		for (int i = 0; i < options.length; i++) {
			b.option(options[i].getKey(), options[i].getValue());
		}
		b.handler(handler);

		return b.connect(ip, port).sync();
	}

}
