package com.storechain.connection.handler;

import java.lang.reflect.Constructor;

import com.storechain.connection.InboundPacket;
import com.storechain.connection.netty.PacketInboundAdapter;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketInboundBinaryFrameHandler<T extends InboundPacket> extends PacketInboundAdapter {

	private Constructor<T> constructor;
	
	private Object[] args;
	
	public WebSocketInboundBinaryFrameHandler(Constructor<T> constructor, Object... args) {
		this.constructor = constructor;
		this.args = args;
	}
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    	if(msg instanceof BinaryWebSocketFrame) {
    		BinaryWebSocketFrame frame = (BinaryWebSocketFrame) msg;
    		T packet = constructor.getParameterCount() > 1 || args.length > 0 ? constructor.newInstance(ByteBufUtil.getBytes(frame.content()), args) : constructor.newInstance(ByteBufUtil.getBytes(frame.content()));
    		packet.decodeHeader();
    		super.channelRead(ctx, packet);
    	}
    }
}
