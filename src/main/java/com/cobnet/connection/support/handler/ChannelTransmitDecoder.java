package com.cobnet.connection.support.handler;

import com.cobnet.connection.support.NettyChannel;
import com.cobnet.interfaces.connection.InputTransmission;
import com.cobnet.interfaces.connection.TransmissionDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public abstract class ChannelTransmitDecoder<T extends NettyChannel, E extends InputTransmission<?>> extends ByteToMessageDecoder implements TransmissionDecoder<T, E>  {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        out.add(decode((T) ctx.channel().attr(NettyChannel.CHANNEL_KEY).get(), in));
    }
}
