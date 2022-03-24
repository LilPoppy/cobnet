package com.cobnet.connection.websocket.handler;

import com.cobnet.connection.support.InboundPacket;
import com.cobnet.connection.support.handler.ChannelPacketDecoder;
import com.cobnet.connection.websocket.WebSocketChannel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class WebSocketPacketDecodeHandler extends ChannelPacketDecoder<WebSocketChannel> {

    @Override
    public InboundPacket decode(WebSocketChannel channel, ByteBuf in) {
        System.out.println(new InboundPacket(Unpooled.copiedBuffer(in.readBytes(in.readableBytes())), channel.getServer()));
        return new InboundPacket(Unpooled.copiedBuffer(in.readBytes(in.readableBytes())), channel.getServer());
    }
}
