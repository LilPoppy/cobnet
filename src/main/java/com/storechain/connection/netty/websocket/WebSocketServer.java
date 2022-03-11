package com.storechain.connection.netty.websocket;

import java.lang.reflect.InvocationTargetException;
import java.util.Map.Entry;

import org.springframework.scheduling.annotation.Async;

import com.storechain.connection.netty.NettyServer;
import com.storechain.connection.netty.websocket.handler.WebSocketInitializeHandler;

import io.netty.channel.ChannelOption;

public class WebSocketServer extends NettyServer {

	public WebSocketServer(String name, int port) {
		super(name, port);
	}
	
	@Async
	@SuppressWarnings("unchecked")
	public void bind(int backlog, final Entry<ChannelOption, Object>... childOptions) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		super.bind(new WebSocketInitializeHandler(this), backlog, childOptions);
	}

}
