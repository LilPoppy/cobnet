package com.storechain.connection.handler;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.session.Session;
import com.storechain.connection.InboundOperation;
import com.storechain.connection.InboundPacket;
import com.storechain.connection.OutboundOperation;
import com.storechain.connection.OutboundPacket;
import com.storechain.connection.netty.NettyServer;
import com.storechain.interfaces.connection.AuthenticatableChannel;
import com.storechain.interfaces.connection.ConnectionListener;
import com.storechain.interfaces.connection.annotation.ConnectionHandler;
import com.storechain.interfaces.security.Operator;
import com.storechain.spring.boot.configuration.SecurityConfiguration;
import com.storechain.utils.DatabaseManager;
import com.storechain.utils.SessionManager;

import io.netty.channel.Channel;

public class AuthenticationPacketHandler implements ConnectionListener{

	protected static Logger log = LoggerFactory.getLogger(UnknownPacketHandler.class);
	
	public final static String AUTHENTICATION_TOKEN_KEY = "token";
	
	private final NettyServer server;
	
	public AuthenticationPacketHandler(NettyServer server) {
		
		this.server = server;
	}
	
	@ConnectionHandler(operation = InboundOperation.AUTHENTICATION)
	@Override
	public void onEvent(Channel channel, InboundPacket packet) {

		if(channel instanceof AuthenticatableChannel) {
			
			AuthenticatableChannel authenticatable = (AuthenticatableChannel) channel;
			
			String token = packet.decodeString();
			
			for(String id : SessionManager.getSessionIds()) {
				
				Session session = DatabaseManager.getManagedSessionRepository().findById(id);
				
				if(session != null && !session.isExpired()) {
					
					String value = session.getAttribute(AUTHENTICATION_TOKEN_KEY);
					
					if(value != null && value.equals(token)) {

						Operator operator = (Operator)((SecurityContext)session.getAttribute(SecurityConfiguration.SESSION_KEY)).getAuthentication().getPrincipal();
						
						authenticatable.setOperator(operator);
						
						token = UUID.randomUUID().toString();
						
						session.setAttribute(AUTHENTICATION_TOKEN_KEY, token);
						
						DatabaseManager.getManagedSessionRepository().save(session);
						
						OutboundPacket out = new OutboundPacket(this.server, OutboundOperation.AUTHENTICATION);
						
						out.encodeString(token);
						
						channel.write(out.getBuf());
						
						log.info(String.format("Channel %s has authenticated successfully as username: %s", channel.remoteAddress().toString(), operator.getUsername()));
						
						return;
					}
				}
			}
			
			log.info(String.format("Channel %s has authentication failed with token: %s.", channel.remoteAddress().toString(), token));
			
			return;
		}
		
		log.warn(String.format("Channel %s try to authenticate without permitted.", channel.remoteAddress().toString()));
	}

	@Override
	public NettyServer getServer() {
		
		return this.server;
	}

}
