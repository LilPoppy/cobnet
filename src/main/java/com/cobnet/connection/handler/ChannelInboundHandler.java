package com.cobnet.connection.handler;

import com.cobnet.interfaces.connection.InputTransmission;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class ChannelInboundHandler<T extends InputTransmission<byte[]>> extends SimpleChannelInboundHandler<T> {

    protected static Logger LOG = LoggerFactory.getLogger(ChannelInboundHandler.class);

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
