package com.cobnet.connection;

import com.cobnet.interfaces.connection.InputTransmission;
import com.cobnet.interfaces.connection.Transmission;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.locks.ReentrantLock;

public class NettyChannel implements Channel {

    public static final AttributeKey<NettyChannel> CHANNEL_KEY = AttributeKey.valueOf("cobnet-channel");

    protected transient final NettyServer<? extends NettyChannel> server;

    protected final Channel channel;

    private final ReentrantLock locker;

    public NettyChannel(NettyServer<? extends NettyChannel> server, Channel channel) {

        this.server = server;
        this.channel = channel;
        this.locker = new ReentrantLock(true);
    }

    public NettyServer<? extends NettyChannel> getServer() {

        return this.server;
    }

    public final void acquireEncodeState() {

        this.locker.lock();
    }

    public final void releaseEncodeState() {

        this.locker.unlock();
    }

    public <T extends Transmission<byte[]>> void write(T packet) {

        this.writeAndFlush(packet.getData());
    }

    public boolean isConnected() {

        return this.server.contains(this) && this.channel.isActive();
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
    public ChannelFuture close() {

        this.server.remove(this);

        return this.channel.close();
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

        this.server.remove(this);

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
        return this.channel.config();
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
        return (InetSocketAddress) this.remoteAddress();
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

    @Override
    public <E> Attribute<E> attr(AttributeKey<E> key) {
        return this.channel.attr(key);
    }

    @Override
    public <E> boolean hasAttr(AttributeKey<E> key) {
        return this.channel.hasAttr(key);
    }
}
