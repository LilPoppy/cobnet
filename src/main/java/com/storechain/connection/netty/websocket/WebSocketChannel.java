package com.storechain.connection.netty.websocket;

import com.storechain.connection.InboundPacket;
import com.storechain.connection.Packet;
import com.storechain.connection.netty.NettyNioSocketChannel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketChannel extends NettyNioSocketChannel<InboundPacket> {

	public WebSocketChannel(WebSocketServer server, Channel channel) {

		super(server, channel, new InboundPacket(Unpooled.buffer(), server.getConfiguration().getDecodeEndian(), server.getConfiguration().getCharset()));
	}
	
	@Override
	public void write(Packet packet) {
		ByteBuf buf = packet.getBuf();
		this.writeAndFlush(new BinaryWebSocketFrame(buf));
	}
	
	public void message(String msg) {
		this.writeAndFlush(new TextWebSocketFrame(msg));
	}

}
