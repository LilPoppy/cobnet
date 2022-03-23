package com.cobnet.connection.handler;

import com.cobnet.connection.InboundPacket;
import com.cobnet.connection.NettyChannel;
import com.cobnet.interfaces.connection.EventListener;
import com.cobnet.spring.boot.controller.handler.InboundOperation;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import io.netty.channel.ChannelHandlerContext;

public class ChannelInboundPacketHandler extends ChannelInboundHandler<InboundPacket> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, InboundPacket input) {

        NettyChannel channel = ctx.channel().attr(NettyChannel.CHANNEL_KEY).get();

        int hits = 0;

        String[] names = ProjectBeanHolder.getSpringContext().getBeanNamesForType(EventListener.class);

        for(String name : names) {

            if(ProjectBeanHolder.getSpringContext().getBean(name, EventListener.class).onEvent(channel, input)) {

                hits += 1;
            }
        }

        if(hits > 0) {

            if(input.getOperation() != InboundOperation.UNKNOWN) {

                LOG.debug(String.format("[IN] [%s(%d)] | %s |", input.getOperation().name(), input.getOperation().code(), input.toString()));
            }
        }  else if(input.getOperation() != InboundOperation.UNKNOWN) {

            LOG.warn(String.format("Unhandle packet: %s(%d)| %s |", input.getOperation().name(), input.getOpcode(), input.toString()));

            //TODO unhandle operation action
        }
    }
}
