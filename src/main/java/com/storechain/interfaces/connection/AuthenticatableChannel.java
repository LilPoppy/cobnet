package com.storechain.interfaces.connection;

import com.storechain.interfaces.security.Operator;

import io.netty.channel.Channel;

public interface AuthenticatableChannel extends Channel {
	
	public Operator getOperator();

	public void setOperator(Operator operator);
	
	public boolean isAuthenticated();
}
