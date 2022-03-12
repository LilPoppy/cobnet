package com.storechain.connection.netty;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.locks.ReentrantLock;

import com.storechain.connection.Packet;
import com.storechain.interfaces.connection.AuthenticatableChannel;
import com.storechain.interfaces.security.Operator;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class NettyNioSocketChannel<T extends Packet> implements AuthenticatableChannel {
	
	public static final AttributeKey<Channel> CLIENT_KEY = AttributeKey.valueOf("StoreChain-Channel");
	
	protected transient final NettyServer server;
	
	protected final Channel channel;
	
	private final ReentrantLock locker;
	
	private final T reader;
	
	private Operator operator;
	
	public NettyNioSocketChannel(NettyServer server, Channel channel, T packet) {
		this.server = server;
		this.channel = channel;
		this.reader =  packet;
		this.locker = new ReentrantLock(true);
    }
	
	public final T getReader() {
		
		return this.reader;
	}
	
	public void write(Packet msg) {
		
		this.writeAndFlush(msg.getBuf());
	}
	
	public boolean isConnected() {
		
		return this.server.contains(this);
	}
	
	@Override
	public ChannelFuture close() {
		
		if(this.server.contains(this)) {
			
			this.server.remove(this);
		}
		
		return this.channel.close();
	}
	
	public ChannelFuture connect(String host, int port) {
		
		return this.connect(InetSocketAddress.createUnresolved(host, port));
	}
	
	public String getFullIPAddress() {
		
		return this.remoteAddress().toString();
	}
	
	public String getIP() {
		
		return getFullIPAddress().split(":")[0].substring(1);
	}
	
	public final void acquireEncoderState() {
		
		this.locker.lock();
	}

	public final void releaseEncodeState() {
		
		this.locker.unlock();
	}

	@Override
	public Operator getOperator() {
		return operator;
	}

	@Override
	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	
	@Override
	public boolean isAuthenticated() {
		
		return this.operator != null;
	}

	@Override
	public <T> Attribute<T> attr(AttributeKey<T> key) {
		return this.channel.attr(key);
	}

	@Override
	public <T> boolean hasAttr(AttributeKey<T> key) {
		return this.channel.hasAttr(key);
	}

	@Override
	public ChannelFuture bind(SocketAddress localAddress) {
		return this.channel.bind(localAddress);
	}

	@Override
	public ChannelFuture connect(SocketAddress remoteAddress) {
		return this.channel.connect(remoteAddress);
	}

	@Override
	public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
		return this.channel.connect(remoteAddress, localAddress);
	}

	@Override
	public ChannelFuture disconnect() {
		return this.channel.disconnect();
	}

	@Override
	public ChannelFuture deregister() {
		return this.channel.deregister();
	}

	@Override
	public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
		return this.channel.bind(localAddress, promise);
	}

	@Override
	public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
		return this.channel.connect(remoteAddress, promise);
	}

	@Override
	public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
		return this.channel.connect(remoteAddress, localAddress, promise);
	}

	@Override
	public ChannelFuture disconnect(ChannelPromise promise) {
		return this.channel.disconnect(promise);
	}

	@Override
	public ChannelFuture close(ChannelPromise promise) {
		
		if(this.server.contains(this)) {
			
			this.server.remove(this);
		}
		
		return this.channel.close(promise);
	}

	@Override
	public ChannelFuture deregister(ChannelPromise promise) {
		return this.channel.deregister(promise);
	}

	@Override
	public ChannelFuture write(Object msg) {
		return this.channel.write(msg);
	}

	@Override
	public ChannelFuture write(Object msg, ChannelPromise promise) {
		return this.channel.write(msg, promise);
	}

	@Override
	public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
		return this.channel.writeAndFlush(msg, promise);
	}

	@Override
	public ChannelFuture writeAndFlush(Object msg) {
		return this.channel.write(msg);
	}

	@Override
	public ChannelPromise newPromise() {
		return this.channel.newPromise();
	}

	@Override
	public ChannelProgressivePromise newProgressivePromise() {
		return this.channel.newProgressivePromise();
	}

	@Override
	public ChannelFuture newSucceededFuture() {
		return this.channel.newSucceededFuture();
	}

	@Override
	public ChannelFuture newFailedFuture(Throwable cause) {
		return this.channel.newFailedFuture(cause);
	}

	@Override
	public ChannelPromise voidPromise() {
		return this.channel.voidPromise();
	}

	@Override
	public int compareTo(Channel o) {
		return this.channel.compareTo(o);
	}

	@Override
	public ChannelId id() {
		return this.channel.id();
	}

	@Override
	public EventLoop eventLoop() {
		return this.channel.eventLoop();
	}

	@Override
	public Channel parent() {
		return this.channel.parent();
	}

	@Override
	public ChannelConfig config() {
		return this.config();
	}

	@Override
	public boolean isOpen() {
		return this.channel.isOpen();
	}

	@Override
	public boolean isRegistered() {
		return this.channel.isRegistered();
	}

	@Override
	public boolean isActive() {
		return this.channel.isActive();
	}

	@Override
	public ChannelMetadata metadata() {
		return this.channel.metadata();
	}

	@Override
	public SocketAddress localAddress() {
		return this.channel.localAddress();
	}

	@Override
	public SocketAddress remoteAddress() {
		return this.channel.remoteAddress();
	}
	
	public InetSocketAddress getRemoteAddress() {
		return (InetSocketAddress) channel.remoteAddress();
	}

	@Override
	public ChannelFuture closeFuture() {
		return this.channel.closeFuture();
	}

	@Override
	public boolean isWritable() {
		return this.channel.isWritable();
	}

	@Override
	public long bytesBeforeUnwritable() {
		return this.channel.bytesBeforeUnwritable();
	}

	@Override
	public long bytesBeforeWritable() {
		return this.channel.bytesBeforeWritable();
	}

	@Override
	public Unsafe unsafe() {
		return this.channel.unsafe();
	}

	@Override
	public ChannelPipeline pipeline() {
		return this.channel.pipeline();
	}

	@Override
	public ByteBufAllocator alloc() {
		return this.channel.alloc();
	}

	@Override
	public Channel read() {
		return this.channel.read();
	}

	@Override
	public Channel flush() {
		return this.channel.flush();
	}
}
