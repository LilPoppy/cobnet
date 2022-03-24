package com.cobnet.connection.websocket.handler;

import com.cobnet.connection.websocket.WebSocketServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketInboundUrlResolvingHandler extends ChannelInboundHandlerAdapter {

    protected Logger LOG = LoggerFactory.getLogger(WebSocketInboundUrlResolvingHandler.class);

    private final WebSocketServer server;

    public WebSocketInboundUrlResolvingHandler(WebSocketServer server) {

        this.server = server;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("@@@@0");
        super.channelRead(ctx, msg);
        System.out.println("@@@@1");
        if (msg instanceof FullHttpRequest request) {
            System.out.println("@@@@2");

            String uri = request.uri();

            int index = uri.indexOf("?");

            if (index > 0) {

                String query = uri.substring(index + 1);
                MultiMap<String> values = new MultiMap<>();
                UrlEncoded.decodeTo(query, values, this.getServer().getCharset());
                request.setUri(uri.substring(0, index));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        LOG.warn(String.format("An unhandled exception occurred when resolving url for %s", ctx.channel().remoteAddress()));

        cause.printStackTrace();

        super.exceptionCaught(ctx, cause);
    }

    public WebSocketServer getServer() {

        return this.server;
    }
}
