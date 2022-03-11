package com.storechain.connection.handler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.storechain.EntryPoint;
import com.storechain.connection.InboundOperation;
import com.storechain.connection.InboundPacket;
import com.storechain.connection.netty.NettySession;
import com.storechain.connection.netty.NettyServer;
import com.storechain.interfaces.connection.ConnectionListener;
import com.storechain.interfaces.connection.annotation.ConnectionHandler;

public class UnknownPacketHandler implements ConnectionListener {
	
	protected static Logger log = LoggerFactory.getLogger(UnknownPacketHandler.class);
	
	private final NettyServer server;
	
	public UnknownPacketHandler(NettyServer server) {
		this.server = server;
	}

	@ConnectionHandler(operation = InboundOperation.UNKNOWN)
	@Override
	public void onEvent(NettySession client, InboundPacket packet) {
		
		if(server.getConfiguration().isKickUnknownPacketUser()) {
			if(client.isConnected()) {
				log.warn(String.format("Kicking %s by sending unknown packet.", client.getFullIPAddress()));
				client.close();	
				server.block(client, new Date(System.currentTimeMillis() + (5 * 1000)));
			}
		}
		
	}

	@Override
	public NettyServer getServer() {
		return server;
	}

}
