package com.storechain.connection.netty;

import com.storechain.connection.InboundPacket;
import com.storechain.connection.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketClient extends NettyClient<NioSocketChannel, InboundPacket> {

	public WebSocketClient(NioSocketChannel c) {
		super(c, new InboundPacket());
	}
	
	@Override
	public void write(Packet packet) {
		ByteBuf buf = packet.getBuf();
		channel.writeAndFlush(new BinaryWebSocketFrame(buf));
	}
	
	public void message(String msg) {
		channel.writeAndFlush(new TextWebSocketFrame(msg));
	}

}
