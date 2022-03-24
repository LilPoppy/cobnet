package com.cobnet.connection.support.handler;

import com.cobnet.connection.support.NettyChannel;
import com.cobnet.interfaces.connection.Transmission;
import com.cobnet.interfaces.connection.TransmissionEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public abstract class ChannelTransmitEncoder<T extends NettyChannel, E extends Transmission<?>> extends MessageToByteEncoder<E> implements TransmissionEncoder<T, E> {

    @Override
    protected void encode(ChannelHandlerContext ctx, E msg, ByteBuf out) throws Exception {

        out.writeBytes(encode((T) ctx.channel().attr(NettyChannel.CHANNEL_KEY).get(), msg));
    }
}
