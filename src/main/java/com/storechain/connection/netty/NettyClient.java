package com.storechain.connection.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.locks.ReentrantLock;

import com.storechain.connection.InboundPacket;
import com.storechain.connection.Packet;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class NettyClient<CH extends Channel, T extends Packet> {
	
	@SuppressWarnings("rawtypes")
	public static final AttributeKey<NettyClient> CLIENT_KEY = AttributeKey.valueOf("StoreChain-Client");
	
	protected final NettyServer server;
	
	protected final CH channel;
	
	private final ReentrantLock locker;
	
	private final T reader;
	
	public NettyClient(NettyServer server, CH c, T packet) {
		this.server = server;
        channel = c;
        reader =  packet;
        locker = new ReentrantLock(true);
    }
	
	public final T getReader() {
		return reader;
	}
	
	public void write(Packet msg) {
		channel.writeAndFlush(msg);
	}
	
	public boolean isConnected() {
		return server.contains(channel);
	}
	
	public void close() {
		if(server.contains(channel)) {
			server.remove(this);
			channel.close();
		}
	}
	
	public String getFullIPAddress() {
		return channel.remoteAddress().toString();
	}
	
	public String getIP() {
		return getFullIPAddress().split(":")[0].substring(1);
	}
	
	public InetSocketAddress getAddress() {
		return (InetSocketAddress) channel.remoteAddress();
	}

	public final void acquireEncoderState() {
		locker.lock();
	}

	public final void releaseEncodeState() {
		locker.unlock();
	}
}
