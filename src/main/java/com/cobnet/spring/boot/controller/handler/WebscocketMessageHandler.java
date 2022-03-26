package com.cobnet.spring.boot.controller.handler;

import com.cobnet.connection.support.InboundPacket;
import com.cobnet.connection.support.NettyChannel;
import com.cobnet.connection.websocket.WebSocketServer;
import com.cobnet.interfaces.connection.EventListener;
import com.cobnet.interfaces.connection.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class WebscocketMessageHandler implements EventListener {

    private static Logger LOG = LoggerFactory.getLogger(WebscocketMessageHandler.class);

    @EventHandler(value = InboundOperation.WEBSOCKET_MESSAGE, allowed = WebSocketServer.class)
    @Override
    public boolean onEvent(NettyChannel channel, InboundPacket packet) {

        //TODO I don't know
        String massage = packet.decodeText();

        LOG.info(String.format("%s: %s", channel.remoteAddress(), massage));

        return true;
    }
}
