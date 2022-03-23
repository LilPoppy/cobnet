package com.cobnet.connection.handler;

import com.cobnet.connection.InboundPacket;
import com.cobnet.connection.NettyChannel;
import com.cobnet.interfaces.connection.EventListener;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import io.netty.channel.ChannelHandlerContext;

public class ChannelInboundPacketHandler extends ChannelInboundHandler<InboundPacket> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, InboundPacket input) {

        NettyChannel channel = ctx.channel().attr(NettyChannel.CHANNEL_KEY).get();

        for(String name : ProjectBeanHolder.getSpringContext().getBeanNamesForType(EventListener.class)) {

            ProjectBeanHolder.getSpringContext().getBean(name, EventListener.class).onEvent(channel, input);
        }

        System.out.println("reading: " + input.getOpcode() + "|" + input + "|");
    }
}
