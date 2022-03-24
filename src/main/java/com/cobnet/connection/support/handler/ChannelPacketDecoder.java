package com.cobnet.connection.support.handler;

import com.cobnet.connection.support.InboundPacket;
import com.cobnet.connection.support.NettyChannel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;

public abstract class ChannelPacketDecoder<T extends NettyChannel> extends ChannelTransmitDecoder<T, InboundPacket> {

    @Override
    public InboundPacket decode(T channel, ByteBuf in) {
        return new InboundPacket(Unpooled.copiedBuffer(in.readBytes(in.readableBytes())), channel.getServer());
    }
}
