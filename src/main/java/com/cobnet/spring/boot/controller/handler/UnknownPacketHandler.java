package com.cobnet.spring.boot.controller.handler;

import com.cobnet.connection.support.InboundPacket;
import com.cobnet.connection.support.NettyChannel;
import com.cobnet.interfaces.connection.AuthenticatableChannel;
import com.cobnet.interfaces.connection.EventListener;
import com.cobnet.interfaces.connection.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class UnknownPacketHandler implements EventListener {

    private static Logger LOG = LoggerFactory.getLogger(UnknownPacketHandler.class);

    @EventHandler(value = InboundOperation.UNKNOWN)
    @Override
    public boolean onEvent(NettyChannel channel, InboundPacket packet) {

        if(channel.isConnected()) {

            LOG.warn(String.format("Kicking %s by sending unknown packet.", channel instanceof AuthenticatableChannel authenticated ? authenticated.getAccount().getUsername() + "(" + channel.getRemoteAddress() +")" : channel.getRemoteAddress()));
            channel.close();
        }

        return false;
    }
}
