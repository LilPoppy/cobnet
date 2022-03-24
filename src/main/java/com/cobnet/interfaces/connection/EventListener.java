package com.cobnet.interfaces.connection;

import com.cobnet.connection.support.InboundPacket;
import com.cobnet.connection.support.NettyChannel;

public interface EventListener {

    public boolean onEvent(NettyChannel channel, InboundPacket packet);

}
