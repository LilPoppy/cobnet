package com.storechain.interfaces.connection;

import com.storechain.connection.InboundPacket;
import com.storechain.connection.netty.NettySession;
import com.storechain.connection.netty.NettyServer;

public interface ConnectionListener {
	
	@SuppressWarnings("rawtypes")
	public void onEvent(NettySession client, InboundPacket packet);

	public NettyServer getServer();
}
