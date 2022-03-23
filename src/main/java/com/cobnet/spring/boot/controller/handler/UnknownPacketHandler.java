package com.cobnet.spring.boot.controller.handler;

import com.cobnet.connection.InboundPacket;
import com.cobnet.connection.NettyChannel;
import com.cobnet.connection.NettyServer;
import com.cobnet.connection.web.WebSocketServer;
import com.cobnet.interfaces.connection.EventListener;
import com.cobnet.interfaces.connection.annotation.EventHandler;
import com.cobnet.interfaces.security.annotation.AccessSecured;
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
