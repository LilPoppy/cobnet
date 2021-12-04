package com.storechain.connection.netty;


import org.apache.log4j.Logger;

import com.storechain.connection.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class PacketInboundAdapter extends ChannelInboundHandlerAdapter {
	
	protected Logger log = Logger.getRootLogger();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		super.channelRead(ctx, msg);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
		ctx.close();
	}
}
