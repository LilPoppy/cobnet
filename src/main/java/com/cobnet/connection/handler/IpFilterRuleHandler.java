package com.cobnet.connection.handler;

import com.cobnet.connection.NettyServer;
import io.netty.handler.ipfilter.IpFilterRule;
import io.netty.handler.ipfilter.IpFilterRuleType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

public class IpFilterRuleHandler<T extends NettyServer<?>> implements IpFilterRule {

	protected static Logger log = LogManager.getLogger(IpFilterRuleHandler.class);

	private final T server;
	
	public IpFilterRuleHandler(T server) {

		this.server = server;
	}

	@Override
	public boolean matches(InetSocketAddress remoteAddress) {
		
		boolean banned = false;//this.server.isBlocked(remoteAddress.getAddress());
		
		if(banned) {

			log.warn(String.format("Blocked %s attemping to take connection.", remoteAddress));
		}

		return banned;
	}

	@Override
	public IpFilterRuleType ruleType() {
		return IpFilterRuleType.REJECT;
	}

}
