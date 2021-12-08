package com.storechain.interfaces.connection;

import com.storechain.connection.InboundPacket;
import com.storechain.connection.netty.NettyClient;
import com.storechain.connection.netty.NettyServer;

public interface Listener {
	
	@SuppressWarnings("rawtypes")
	public void onEvent(NettyClient client, InboundPacket packet);

	public NettyServer getServer();
}
