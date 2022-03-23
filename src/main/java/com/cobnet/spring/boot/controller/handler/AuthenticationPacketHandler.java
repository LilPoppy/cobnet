package com.cobnet.spring.boot.controller.handler;

import com.cobnet.connection.InboundPacket;
import com.cobnet.connection.NettyChannel;
import com.cobnet.connection.NettyServer;
import com.cobnet.interfaces.connection.EventListener;
import com.cobnet.interfaces.connection.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class AuthenticationPacketHandler<T extends NettyServer<?>> implements EventListener<T> {

	private static Logger LOG = LoggerFactory.getLogger(AuthenticationPacketHandler.class);
	
	public final static String AUTHENTICATION_TOKEN_KEY = "token";

	private final T server;

	public AuthenticationPacketHandler(T server) {
		this.server = server;
	}

	@EventHandler(InboundOperation.AUTHENTICATION)
	@Override
	public void onEvent(NettyChannel channel, InboundPacket packet) {
		System.out.println("on authen");
	}

	@Override
	public T getServer() {

		return this.server;
	}

}
