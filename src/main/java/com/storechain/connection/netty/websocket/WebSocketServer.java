package com.storechain.connection.netty.websocket;

import java.lang.reflect.InvocationTargetException;
import java.util.Map.Entry;

import org.springframework.scheduling.annotation.Async;

import com.storechain.connection.netty.NettyServer;
import com.storechain.connection.netty.websocket.handler.WebSocketChannelInitializeHandler;

import io.netty.channel.ChannelOption;

public class WebSocketServer extends NettyServer {

	public WebSocketServer(String name, int port) {
		super(name, port);
	}
	
	@Async
	public void bind(int backlog, final Entry<ChannelOption<?>, ?>... childOptions) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		super.bind(new WebSocketChannelInitializeHandler(this), backlog, childOptions);
	}

}
