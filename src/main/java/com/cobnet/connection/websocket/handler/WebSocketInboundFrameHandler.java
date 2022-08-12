package com.cobnet.connection.websocket.handler;

import com.cobnet.common.enums.Endian;
import com.cobnet.connection.support.NettyChannel;
import com.cobnet.connection.websocket.WebSocketChannel;
import com.cobnet.spring.boot.controller.handler.InboundOperation;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketInboundFrameHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(ctx.channel().attr(NettyChannel.CHANNEL_KEY).get() instanceof WebSocketChannel channel) {

            if(msg instanceof BinaryWebSocketFrame binary) {

                super.channelRead(ctx, binary.content());
                return;
            }

            if(msg instanceof TextWebSocketFrame text) {

                ByteBuf buf = ctx.alloc().buffer(4);

                byte[] bs = text.text().getBytes(channel.getServer().getCharset());

                int opcode = (int)(InboundOperation.WEBSOCKET_MESSAGE.code() & 0xFFFFFFFFL);
                int length = (int)(bs.length & 0xFFFFFFFFL);

                if(channel.getServer().getEndian() == Endian.LITTLE) {

                    buf.writeIntLE(opcode);
                    buf.writeIntLE(length);

                } else {

                    buf.writeInt(opcode);
                    buf.writeInt(length);
                }

                buf.writeBytes(bs);

                super.channelRead(ctx, buf);
            }
        }
    }
}
