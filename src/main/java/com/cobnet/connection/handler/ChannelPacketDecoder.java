package com.cobnet.connection.handler;

import com.cobnet.connection.InboundPacket;
import com.cobnet.connection.NettyChannel;
import com.cobnet.connection.TransmissionDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ChannelPacketDecoder extends TransmissionDecoder<InboundPacket> {

    @Override
    protected InboundPacket decode(NettyChannel channel, ByteBuf in) {
        return new InboundPacket(Unpooled.copiedBuffer(in.readBytes(in.readableBytes())), channel.getServer());
    }
}
