package com.storechain.connection.netty.websocket.handler;

import javax.net.ssl.SSLHandshakeException;

import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.storechain.connection.netty.handler.InboundHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class WebSocketInboundUrlHandler extends InboundHandler  {

	protected Logger log = LoggerFactory.getLogger(InboundHandler.class);
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    	super.channelRead(ctx, msg);
    	
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String uri = request.uri();
            int idx = uri.indexOf("?");
            if (idx > 0) {
                String query = uri.substring(idx + 1);
                MultiMap<String> values = new MultiMap<String>();
                UrlEncoded.decodeTo(query, values, "UTF-8");
                request.setUri(uri.substring(0, idx));
            }
        }
    }
    
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		
		ctx.close();

		if(cause instanceof SSLHandshakeException) {
			log.warn(String.format("%s has sent unknown package.", ctx.channel().remoteAddress()));
			return;
		}
		
		super.exceptionCaught(ctx, cause);
		
	}
}
