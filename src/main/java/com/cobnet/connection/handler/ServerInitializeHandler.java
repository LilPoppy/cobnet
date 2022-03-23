package com.cobnet.connection.handler;

import com.cobnet.connection.NettyChannel;
import com.cobnet.connection.NettyServer;
import com.cobnet.interfaces.connection.ServerInitializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.logging.LoggingHandler;

public class ServerInitializeHandler<T extends NettyServer<E>, E extends NettyChannel> extends LoggingHandler implements ServerInitializer<T> {

    private final T server;

    public ServerInitializeHandler(T server) {

        super();

        this.server = server;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        super.channelRegistered(ctx);

        T server = this.getServer();
    }

    @Override
    public T getServer() {

        return this.server;
    }
}
