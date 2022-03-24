package com.cobnet.interfaces.connection;

import com.cobnet.connection.support.NettyServer;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;

public interface ServerInitializer<T extends NettyServer<?>> extends ChannelInboundHandler, ChannelOutboundHandler {

    public T getServer();
}
