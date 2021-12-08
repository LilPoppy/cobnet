package com.storechain.connection.netty.websocket.handler;

import org.apache.log4j.Logger;

import com.storechain.EntryPoint;
import com.storechain.connection.InboundOperation;
import com.storechain.connection.InboundPacket;
import com.storechain.connection.netty.NettyClient;
import com.storechain.connection.netty.websocket.WebSocketClient;
import com.storechain.connection.netty.websocket.WebSocketServer;
import com.storechain.interfaces.connection.Listener;
import com.storechain.interfaces.connection.annotation.Handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class WebSocketInboundPacketHandler extends SimpleChannelInboundHandler<InboundPacket> {
	
	private final WebSocketServer server;
	
	public WebSocketInboundPacketHandler(WebSocketServer server) {
		this.server = server;
	}

	protected static Logger log = Logger.getLogger(WebSocketInboundPacketHandler.class);
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, InboundPacket packet) throws Exception {
		
    	InboundOperation operation = packet.getOperation();
    	
        
        NettyClient client = ctx.channel().attr(WebSocketClient.CLIENT_KEY).get();
        
        if(operation == InboundOperation.UNKNOWN) {
        	
        	log.fatal(String.format("Unknown packet:[(%d) %s]", packet.getOpcode(), packet.toString()));
        }
        
        int hitCount = 0;
        
    	for(Listener listener : server.getListeners()) {

        	Handler handler = server.getHandler(listener);
        	
        	if(handler.operation() == operation) {
        		listener.onEvent(client, packet);
        		hitCount++;
        	}
        	
        	for(InboundOperation op : handler.operations()) {
        		if(op != handler.operation()) {
        			listener.onEvent(client, packet);
        			hitCount++;
        		}
        	}
        }
    	
    	
        if(hitCount > 0) {
        	
            if(server.getConfiguration().isLogChannelRead() && operation != InboundOperation.UNKNOWN) {
            	
            	log.info(String.format("[IN] [%s]", packet.getOperation()));
            }
        }  else if(operation != InboundOperation.UNKNOWN) {
        	
    		log.warn(String.format("Unhandle packet: %s(%d)[%s]", operation.name(), packet.getOpcode(), packet.toString()));
    	}
		
	}
}
