package com.storechain.connection.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.locks.ReentrantLock;

import com.storechain.connection.InboundPacket;
import com.storechain.connection.Packet;
import com.storechain.interfaces.security.Operator;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class NettySession<CH extends Channel, T extends Packet>  {
	
	@SuppressWarnings("rawtypes")
	public static final AttributeKey<NettySession> CLIENT_KEY = AttributeKey.valueOf("StoreChain-Session");
	
	protected transient final NettyServer server;
	
	protected final CH channel;
	
	private final ReentrantLock locker;
	
	private final T reader;
	
	private Operator operator;
	
	public NettySession(NettyServer server, CH channel, T packet) {
		this.server = server;
		this.channel = channel;
		this.reader =  packet;
		this.locker = new ReentrantLock(true);

    }
	
	public final T getReader() {
		return this.reader;
	}
	
	public void write(Packet msg) {
		this.channel.writeAndFlush(msg);
	}
	
	public boolean isConnected() {
		return this.server.contains(channel);
	}
	
	public void close() {
		if(this.server.contains(channel)) {
			this.server.remove(this);
			this.channel.close();
		}
	}
	
	public String getFullIPAddress() {
		return this.channel.remoteAddress().toString();
	}
	
	public String getIP() {
		return getFullIPAddress().split(":")[0].substring(1);
	}
	
	public InetSocketAddress getAddress() {
		return (InetSocketAddress) channel.remoteAddress();
	}

	public final void acquireEncoderState() {
		this.locker.lock();
	}

	public final void releaseEncodeState() {
		this.locker.unlock();
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	
	public boolean isAuthenticated() {
		
		return this.getOperator() != null;
	}
}
