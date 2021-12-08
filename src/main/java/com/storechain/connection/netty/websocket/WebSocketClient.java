package com.storechain.connection.netty.websocket;

import com.storechain.connection.InboundPacket;
import com.storechain.connection.Packet;
import com.storechain.connection.netty.NettyClient;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketClient extends NettyClient<NioSocketChannel, InboundPacket> {

	public WebSocketClient(WebSocketServer server, NioSocketChannel c) {
		super(server, c, new InboundPacket(Unpooled.buffer(), server.getConfiguration().getDecodeEndian(), server.getConfiguration().getCharset()));
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
