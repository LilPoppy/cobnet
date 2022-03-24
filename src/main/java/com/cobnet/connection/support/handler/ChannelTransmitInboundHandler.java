package com.cobnet.connection.support.handler;

import com.cobnet.interfaces.connection.InputTransmission;
import com.cobnet.interfaces.connection.TransmissionInboundHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@ChannelHandler.Sharable
public abstract class ChannelTransmitInboundHandler<T extends InputTransmission<?>> extends SimpleChannelInboundHandler<T> implements TransmissionInboundHandler<T> {

    protected static Logger LOG = LoggerFactory.getLogger(ChannelTransmitInboundHandler.class);

    @Override
    public abstract void channelRead0(ChannelHandlerContext ctx, T input);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        if (cause instanceof IOException) {

            LOG.info("Client forcibly closed.");

        } else {
            cause.printStackTrace();
        }
    }
}
