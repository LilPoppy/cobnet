package com.cobnet.interfaces.connection;

import com.cobnet.connection.support.NettyChannel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;

public interface TransmissionDecoder<T extends NettyChannel, E extends InputTransmission<?>> extends ChannelHandler {

    public E decode(T channel, ByteBuf in);
}
