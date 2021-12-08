package com.storechain.connection.netty.websocket.handler;

import com.storechain.connection.InboundPacket;
import com.storechain.connection.netty.NettyServer;
import com.storechain.connection.netty.handler.InboundHandler;
import com.storechain.interfaces.connection.PacketBuilder;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public class WebSocketInboundBinaryFrameHandler<T extends InboundPacket> extends InboundHandler {

	private final NettyServer server;
	private final PacketBuilder<T> builder;
	
	public WebSocketInboundBinaryFrameHandler(NettyServer server, PacketBuilder<T> builder) {
		this.server = server;
		this.builder = builder;
	}
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    	if(msg instanceof BinaryWebSocketFrame) {
    		BinaryWebSocketFrame frame = (BinaryWebSocketFrame) msg;
    		T packet = builder.build(ByteBufUtil.getBytes(frame.content()), server.getConfiguration().getDecodeEndian(), server.getConfiguration().getCharset());
    		super.channelRead(ctx, packet);
    	}
    }
}
