package com.cobnet.spring.boot.controller.handler;

import com.cobnet.connection.support.InboundPacket;
import com.cobnet.connection.support.NettyChannel;
import com.cobnet.interfaces.connection.EventListener;
import com.cobnet.interfaces.connection.annotation.EventHandler;
import org.springframework.stereotype.Controller;

@Controller
public class UnknownPacketHandler implements EventListener {

    @EventHandler(value = InboundOperation.UNKNOWN)
    @Override
    public boolean onEvent(NettyChannel channel, InboundPacket packet) {

        System.out.println("on event");

        return false;
    }
}
