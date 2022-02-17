package com.storechain.interfaces.connection;

import com.storechain.spring.boot.configuration.NettyConfiguration;

public interface NettyServerProvider {
	
	void provide(NettyConfiguration.ServerConfiguration config);

}
