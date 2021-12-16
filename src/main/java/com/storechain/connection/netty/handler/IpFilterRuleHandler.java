package com.storechain.connection.netty.handler;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.storechain.connection.netty.NettyServer;

import io.netty.handler.ipfilter.IpFilterRule;
import io.netty.handler.ipfilter.IpFilterRuleType;

public class IpFilterRuleHandler implements IpFilterRule {

	protected static Logger log = LogManager.getLogger(IpFilterRuleHandler.class);
	private final NettyServer server;
	
	public IpFilterRuleHandler(NettyServer server) {
		this.server = server;
	}

	
	@Override
	public boolean matches(InetSocketAddress remoteAddress) {
		
		boolean banned = this.server.isBlocked(remoteAddress.getAddress());
		
		if(server.getConfiguration().isLogBannedIpActive() && banned) {
			log.warn(String.format("Blocked %s attemping to take connection.", remoteAddress));
		}

		return banned;
	}

	@Override
	public IpFilterRuleType ruleType() {
		return IpFilterRuleType.REJECT;
	}

}
