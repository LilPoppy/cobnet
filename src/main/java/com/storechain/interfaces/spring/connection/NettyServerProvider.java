package com.storechain.interfaces.spring.connection;

import com.storechain.spring.boot.configuration.NettyConfiguration;

public interface NettyServerProvider {
	
	void providing(NettyConfiguration.ServerConfiguration config);

}
