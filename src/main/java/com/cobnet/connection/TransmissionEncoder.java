package com.cobnet.connection;

import com.cobnet.interfaces.connection.Transmission;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public abstract class TransmissionEncoder<T extends Transmission<byte[]>> extends MessageToByteEncoder<T> {

    @Override
    protected void encode(ChannelHandlerContext ctx, T msg, ByteBuf out) throws Exception {

        out.writeBytes(encode(ctx.channel().attr(NettyChannel.CHANNEL_KEY).get(), msg));
    }

    protected abstract byte[] encode(NettyChannel channel, T msg);
}
