package com.cobnet.connection.websocket.handler;

import com.cobnet.connection.support.OutboundPacket;
import com.cobnet.connection.support.handler.ChannelPacketEncoder;
import com.cobnet.connection.websocket.WebSocketChannel;
import io.netty.channel.ChannelHandler;

public class WebSocketPacketEncodeHandler extends ChannelPacketEncoder<WebSocketChannel> {

    @Override
    public byte[] encode(WebSocketChannel channel, OutboundPacket msg) {

        return msg.getData();
    }
}
