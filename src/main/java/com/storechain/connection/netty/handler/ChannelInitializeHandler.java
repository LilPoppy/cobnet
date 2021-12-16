package com.storechain.connection.netty.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.storechain.connection.netty.NettyClient;
import com.storechain.connection.netty.NettyServer;
import com.storechain.interfaces.connection.NettyClientConverter;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ipfilter.RuleBasedIpFilter;

public class ChannelInitializeHandler<T extends Channel>
		extends ChannelInitializer<T> {

	protected static Logger log = LogManager.getRootLogger();
	
	@SuppressWarnings("rawtypes")
	private final NettyClientConverter converter;
	
	private final NettyServer server;
	
	 @SuppressWarnings("rawtypes")
	public ChannelInitializeHandler(NettyServer server, NettyClientConverter converter) {
		this.server = server;
		this.converter = converter;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initChannel(T ch) throws Exception {
		
		ch.attr(NettyClient.CLIENT_KEY).set(converter.transform(server, ch));
		
		ChannelPipeline pipe = ch.pipeline();
        pipe.addFirst(new RuleBasedIpFilter(new IpFilterRuleHandler(this.server)));
	}
}
