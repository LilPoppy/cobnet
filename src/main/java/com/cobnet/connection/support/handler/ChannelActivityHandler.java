package com.cobnet.connection.support.handler;

import com.cobnet.connection.support.NettyChannel;
import com.cobnet.connection.support.NettyServer;
import com.cobnet.interfaces.connection.AuthenticatableChannel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public abstract class ChannelActivityHandler<T extends NettyServer<E>, E extends NettyChannel> extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ChannelActivityHandler.class);

    private final T server;

    public ChannelActivityHandler(T server) {

        this.server = server;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        NettyChannel channel = ctx.channel().attr(NettyChannel.CHANNEL_KEY).get();

        LOG.info(String.format("%s has connected to server of %s.", channel instanceof AuthenticatableChannel authenticated && authenticated.getAccount() != null ? authenticated.getAccount().getUsername() : channel.remoteAddress(), this.getServer().getName()));

        try {

            channelActive((E)channel);

        } finally {

            server.add(ctx.channel());
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        NettyChannel channel = ctx.channel().attr(NettyChannel.CHANNEL_KEY).get();

        LOG.info(String.format("%s has disconnected from server of %s.", channel instanceof AuthenticatableChannel authenticated && authenticated.getAccount() != null ? authenticated.getAccount().getUsername() : channel.remoteAddress(), this.getServer().getName()));

        try {

            channelInactive((E)channel);

        } finally {
            server.remove(ctx.channel());
        }
    }

    protected abstract void channelActive(E channel);

    protected abstract void channelInactive(E channel);

    public T getServer() {

        return this.server;
    }
}
