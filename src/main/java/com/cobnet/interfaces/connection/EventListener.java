package com.cobnet.interfaces.connection;

import com.cobnet.connection.InboundPacket;
import com.cobnet.connection.NettyChannel;
import com.cobnet.connection.NettyServer;
import io.netty.channel.Channel;

public interface EventListener<T extends NettyServer<? extends NettyChannel>> {

    public void onEvent(NettyChannel channel, InboundPacket packet);

    public T getServer();

}
