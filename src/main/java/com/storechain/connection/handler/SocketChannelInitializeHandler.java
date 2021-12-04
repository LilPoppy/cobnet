package com.storechain.connection.handler;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

import com.storechain.connection.Packet;
import com.storechain.connection.netty.NettyClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public abstract class SocketChannelInitializeHandler<CH extends Channel, T extends Packet, C extends NettyClient<CH, T>>
		extends ChannelInitializer<CH> {

	protected static Logger log = Logger.getRootLogger();
	
	private Constructor<C> constructor; 
	
	private Object[] args;
	
	public SocketChannelInitializeHandler(Constructor<C> contructor, Object... args) {
		this.constructor = contructor;
		this.args = args;
	}

	@Override
	protected void initChannel(CH ch) throws Exception {
		
		ch.attr(NettyClient.CLIENT_KEY).set(constructor.getParameterCount() > 1 || args.length > 0 ? constructor.newInstance(ch, args) : constructor.newInstance(ch));
	}
}
