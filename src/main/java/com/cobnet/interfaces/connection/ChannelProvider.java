package com.cobnet.interfaces.connection;

import com.cobnet.connection.NettyChannel;
import com.cobnet.connection.NettyServer;
import io.netty.channel.Channel;

public interface ChannelProvider<T extends NettyServer<E>, E extends NettyChannel> {

    E provide(T server, Channel channel);
}
