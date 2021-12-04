package com.storechain.connection.netty;

import java.util.concurrent.locks.ReentrantLock;

import com.storechain.connection.InboundPacket;
import com.storechain.connection.Packet;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class NettyClient<CH extends Channel, T extends Packet> {
	
	public static final AttributeKey<NettyClient> CLIENT_KEY = AttributeKey.valueOf("StoreChain");
	
	protected final CH channel;
	
	private final ReentrantLock lock;
	
	private final T reader;
	
	public NettyClient(CH c, T packet) {
        channel = c;
        reader =  packet;
        lock = new ReentrantLock(true);
    }
	
	public final T getReader() {
		return reader;
	}
	
	public void write(Packet msg) {
		channel.writeAndFlush(msg);
	}
	
	public void close() {
		channel.close();
	}
	
	public String getFullIPAddress() {
		return channel.remoteAddress().toString();
	}
	
	public String getIP() {
		return getFullIPAddress().split(":")[0].substring(1);
	}

	public final void acquireEncoderState() {
		lock.lock();
	}

	public final void releaseEncodeState() {
		lock.unlock();
	}
}
