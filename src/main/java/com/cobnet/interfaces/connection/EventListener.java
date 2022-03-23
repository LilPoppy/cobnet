package com.cobnet.interfaces.connection;

import com.cobnet.connection.InboundPacket;
import com.cobnet.connection.NettyChannel;

public interface EventListener {

    public boolean onEvent(NettyChannel channel, InboundPacket packet);

}
