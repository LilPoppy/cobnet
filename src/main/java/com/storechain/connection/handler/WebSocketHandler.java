package com.storechain.connection.handler;

import org.apache.log4j.Logger;
import com.storechain.configuration.ServerConfiguration;
import com.storechain.connection.InboundPacket;
import com.storechain.connection.OutboundPacket;
import com.storechain.connection.netty.NettyChannelHandlerPool;
import com.storechain.connection.netty.NettyClient;
import com.storechain.connection.netty.WebSocketClient;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<InboundPacket> {

	protected static Logger log = Logger.getLogger(WebSocketHandler.class);
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, InboundPacket packet) throws Exception {
		
        if(ServerConfiguration.ENABLE_LOG_WEBSOCKET_CHANNEL_READ) {
        	log.info(String.format("[IN] [%s]", packet.getHeader()));
        }
        
        NettyClient client = ctx.channel().attr(WebSocketClient.CLIENT_KEY).get();
        
        client.write(new OutboundPacket(packet.getData()));
		
	}
	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        if (ServerConfiguration.ENABLE_LOG_WEBSOCKET_CHANNEL_ACTIVE) {
        	log.info(String.format("%s has connected to websocket.", ctx.channel().remoteAddress()));
        }
        
        NettyChannelHandlerPool.CHANNEL_GROUP.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        if (ServerConfiguration.ENABLE_LOG_WEBSOCKET_CHANNEL_INACTIVE) {
        	log.info(String.format("%s has disconnected from websocket.", ctx.channel().remoteAddress()));
        }

        NettyChannelHandlerPool.CHANNEL_GROUP.remove(ctx.channel());
    }


	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		
		ctx.close();
		
		if(cause instanceof DecoderException) {
			log.warn(String.format("%s has occur decode exception.", ctx.channel().remoteAddress()));
			return;
		}
		
		super.exceptionCaught(ctx, cause);
		
	}
}
