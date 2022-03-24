package com.cobnet.connection.websocket.handler;

import com.cobnet.common.Endian;
import com.cobnet.connection.support.NettyChannel;
import com.cobnet.connection.websocket.WebSocketChannel;
import com.cobnet.spring.boot.controller.handler.InboundOperation;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.nio.CharBuffer;

public class WebSocketFrameInboundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("@@@@3");
        if(ctx.channel().attr(NettyChannel.CHANNEL_KEY).get() instanceof WebSocketChannel channel) {

            if(msg instanceof BinaryWebSocketFrame binary) {
                System.out.println("binary");
                super.channelRead(ctx, binary.content());
                return;
            }

            if(msg instanceof TextWebSocketFrame text) {



                ByteBuf buf = ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(text.text()), channel.getServer().getCharset());
                System.out.println(buf);
                if(channel.getServer().getEndian() == Endian.BIG) {

                    buf.setLong(0, InboundOperation.WEBSOCKET_MESSAGE.code());

                } else {

                    buf.setLong(0, InboundOperation.WEBSOCKET_MESSAGE.code());
                }

                super.channelRead(ctx, buf);
            }
        }
    }
}
