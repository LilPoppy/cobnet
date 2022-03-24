package com.cobnet.connection.websocket.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLEngine;

public class WebSocketSslHandler extends SslHandler {

    protected Logger LOG = LoggerFactory.getLogger(WebSocketSslHandler.class);

    public WebSocketSslHandler(SSLEngine engine) {
        super(engine);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        LOG.warn(String.format("An unhandled exception occurred when handling ssl for %s", ctx.channel().remoteAddress()));

        cause.printStackTrace();

        super.exceptionCaught(ctx, cause);

    }
}
