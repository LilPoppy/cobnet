package com.cobnet.connection.support.handler;

import com.cobnet.connection.support.NettyChannel;
import com.cobnet.connection.support.OutboundPacket;
import io.netty.channel.ChannelHandler;

public abstract class ChannelPacketEncoder<T extends NettyChannel> extends ChannelTransmitEncoder<T, OutboundPacket> {

    @Override
    public byte[] encode(T channel, OutboundPacket msg) {

        return msg.getData();
    }
}
