package com.cobnet.connection;

import com.cobnet.interfaces.connection.InputTransmission;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public abstract class TransmissionDecoder<T extends InputTransmission<byte[]>> extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        out.add(decode(ctx.channel().attr(NettyChannel.CHANNEL_KEY).get(), in));
    }

    protected abstract T decode(NettyChannel channel, ByteBuf in);
}
