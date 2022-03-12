package com.storechain.connection.netty.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.storechain.connection.netty.NettyNioSocketChannel;
import com.storechain.connection.netty.NettyServer;
import com.storechain.interfaces.connection.NettyChannelBuilder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.ipfilter.RuleBasedIpFilter;

@Sharable
public class ChannelInitializeHandler<T extends Channel> extends ChannelInitializer<T> {

	protected static Logger log = LogManager.getRootLogger();
	
	private final NettyChannelBuilder<T,?> builder;
	
	private final NettyServer server;
	
	public ChannelInitializeHandler(NettyServer server, NettyChannelBuilder<T,?> builder) {
		this.server = server;
		this.builder = builder;
	}

	@Override
	protected void initChannel(T channel) throws Exception {
		
		channel.attr(NettyNioSocketChannel.CLIENT_KEY).set(builder.build(server, channel));
		
		ChannelPipeline pipe = channel.pipeline();
		
        pipe.addFirst(new RuleBasedIpFilter(new IpFilterRuleHandler(this.server)));
	}
	
	protected NettyServer getServer() {
		
		return this.server;
	}
}
