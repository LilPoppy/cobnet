package com.storechain.connection.handler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.storechain.connection.InboundOperation;
import com.storechain.connection.InboundPacket;
import com.storechain.connection.netty.NettyNioSocketChannel;
import com.storechain.connection.netty.NettyServer;
import com.storechain.interfaces.connection.ConnectionListener;
import com.storechain.interfaces.connection.annotation.ConnectionHandler;

import io.netty.channel.Channel;

public class UnknownPacketHandler implements ConnectionListener {
	
	protected static Logger log = LoggerFactory.getLogger(UnknownPacketHandler.class);
	
	private final NettyServer server;
	
	public UnknownPacketHandler(NettyServer server) {
		this.server = server;
	}

	@ConnectionHandler(operation = InboundOperation.UNKNOWN)
	@Override
	public void onEvent(Channel channel, InboundPacket packet) {
		
		if(!server.getConfiguration().isAllowUnknownPacket()) {
			
			NettyNioSocketChannel<?> target = (NettyNioSocketChannel<?>) channel;
			
			if(target.isConnected()) {
				
				log.warn(String.format("Kicking %s by sending unknown packet.", target.getFullIPAddress()));
				target.close();	
				server.block(target, new Date(System.currentTimeMillis() + (5 * 1000)));
			}
		}
		
	}

	@Override
	public NettyServer getServer() {
		
		return this.server;
	}

}
