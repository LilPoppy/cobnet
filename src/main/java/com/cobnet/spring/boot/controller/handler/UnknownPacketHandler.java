package com.cobnet.spring.boot.controller.handler;

import com.cobnet.connection.InboundPacket;
import com.cobnet.connection.NettyChannel;
import com.cobnet.connection.NettyServer;
import com.cobnet.interfaces.connection.EventListener;
import com.cobnet.interfaces.connection.annotation.EventHandler;
import com.cobnet.interfaces.security.annotation.AccessSecured;
import org.springframework.stereotype.Controller;

@Controller
public class UnknownPacketHandler<T extends NettyServer<?>> implements EventListener<T> {

    private final T server;

    public UnknownPacketHandler(T server) {

        this.server = server;
    }

    @AccessSecured(permissions = "asdasd")
    @EventHandler(InboundOperation.UNKNOWN)
    @Override
    public void onEvent(NettyChannel channel, InboundPacket packet) {
        System.out.println("on event");
    }

    @Override
    public T getServer() {

        return this.server;
    }
}
