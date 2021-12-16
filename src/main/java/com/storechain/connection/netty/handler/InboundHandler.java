package com.storechain.connection.netty.handler;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class InboundHandler extends ChannelInboundHandlerAdapter {
	
	protected Logger log = LoggerFactory.getLogger(InboundHandler.class);

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
		ctx.close();
	}
}
