package com.cobnet.interfaces.connection;

import io.netty.channel.ChannelInboundHandler;

public interface TransmissionInboundHandler<T extends InputTransmission<?>> extends ChannelInboundHandler {
}
