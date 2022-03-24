package com.cobnet.interfaces.connection;

import com.cobnet.connection.support.NettyChannel;
import io.netty.channel.ChannelHandler;

public interface TransmissionEncoder<T extends NettyChannel, E extends Transmission<?>> extends ChannelHandler {

    public byte[] encode(T channel, E msg);
}
