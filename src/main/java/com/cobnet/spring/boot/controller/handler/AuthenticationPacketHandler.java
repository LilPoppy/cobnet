package com.cobnet.spring.boot.controller.handler;

import com.cobnet.connection.support.InboundPacket;
import com.cobnet.connection.support.NettyChannel;
import com.cobnet.interfaces.connection.EventListener;
import com.cobnet.interfaces.connection.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class AuthenticationPacketHandler implements EventListener {

	private static Logger LOG = LoggerFactory.getLogger(AuthenticationPacketHandler.class);
	
	public final static String AUTHENTICATION_TOKEN_KEY = "token";

	@EventHandler(InboundOperation.AUTHENTICATION)
	@Override
	public boolean onEvent(NettyChannel channel, InboundPacket packet) {

		System.out.println("on authen");

		return true;
	}


}
