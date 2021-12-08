package com.storechain.connection.handler;

import java.util.Date;

import org.apache.log4j.Logger;

import com.storechain.EntryPoint;
import com.storechain.connection.InboundOperation;
import com.storechain.connection.InboundPacket;
import com.storechain.connection.netty.NettyClient;
import com.storechain.connection.netty.NettyServer;
import com.storechain.interfaces.connection.Listener;
import com.storechain.interfaces.connection.annotation.Handler;

public class UnknownPacketHandler implements Listener {
	
	protected static Logger log = Logger.getLogger(UnknownPacketHandler.class);
	
	private final NettyServer server;
	
	public UnknownPacketHandler(NettyServer server) {
		this.server = server;
	}

	@Handler(operation = InboundOperation.UNKNOWN)
	@Override
	public void onEvent(NettyClient client, InboundPacket packet) {
		
		if(server.getConfiguration().isKickUnknownPacketUser()) {
			if(client.isConnected()) {
				log.warn(String.format("Kicking %s by sending unknown packet.", client.getFullIPAddress()));
				client.close();	
				server.block(client, new Date(System.currentTimeMillis() + (15 * 1000)));
			}
		}
		
	}

	@Override
	public NettyServer getServer() {
		return server;
	}

}
