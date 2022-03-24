package com.cobnet.connection.support.handler;

import com.cobnet.connection.support.NettyChannel;
import com.cobnet.connection.support.NettyServer;
import com.cobnet.interfaces.connection.ServerInitializer;
import io.netty.channel.ChannelHandler;
import io.netty.handler.logging.LoggingHandler;

@ChannelHandler.Sharable
public abstract class ServerInitializerHandler<T extends NettyServer<E>, E extends NettyChannel> extends LoggingHandler implements ServerInitializer<T> {

    private final T server;

    public ServerInitializerHandler(T server) {

        super();

        this.server = server;
    }

    @Override
    public T getServer() {

        return this.server;
    }
}
