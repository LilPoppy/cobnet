package com.cobnet.connection.support.handler;

import com.cobnet.connection.support.NettyChannel;
import com.cobnet.connection.support.NettyServer;
import com.cobnet.interfaces.connection.ChannelProvider;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ipfilter.RuleBasedIpFilter;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

@ChannelHandler.Sharable
public abstract class ChannelInitializerHandler<T extends NettyServer<E>, E extends NettyChannel> extends ChannelInitializer<NioSocketChannel> {

    private final T server;

    public ChannelInitializerHandler(T server) {

        this.server = server;
    }

    @Override
    protected void initChannel(NioSocketChannel channel) throws Exception {

        E wrapped = this.getProvider().provide(this.server, channel);

        channel.attr(NettyChannel.CHANNEL_KEY).set(wrapped);

        beforeInitChannel(wrapped);

        ChannelPipeline pipe = channel.pipeline();

        if(this.server.getActivityHandler() != null) {

            pipe.addLast(this.server.getActivityHandler());
        }

        if(this.server.getDecoder() != null) {

            pipe.addLast(this.server.getDecoder().get());
        }

        if(this.server.getInboundHandler() != null) {

            pipe.addLast(this.server.getInboundHandler());
        }

        if(this.server.getEncoder() != null) {

            pipe.addLast(this.server.getEncoder().get());
        }

        this.afterInitChannel(wrapped);

        pipe.addFirst(new RuleBasedIpFilter(new IpFilterRuleHandler<T>(this.server)));
    }

    protected abstract void beforeInitChannel(E channel);

    protected abstract void afterInitChannel(E channel) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException;

    protected abstract ChannelProvider<T, E> getProvider();

    public T getServer() {

        return this.server;
    }
}
