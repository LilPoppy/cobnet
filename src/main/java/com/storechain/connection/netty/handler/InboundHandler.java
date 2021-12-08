package com.storechain.connection.netty.handler;


import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class InboundHandler extends ChannelInboundHandlerAdapter {
	
	protected Logger log = Logger.getRootLogger();

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
		ctx.close();
	}
}
