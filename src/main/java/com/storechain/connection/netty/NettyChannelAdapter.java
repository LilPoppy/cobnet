package com.storechain.connection.netty;

import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.storechain.KeyValuePair;
import com.storechain.connection.Packet;
import com.storechain.connection.handler.SocketChannelInitializeHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyChannelAdapter {

	protected static Logger log = Logger.getRootLogger();
	
	@SuppressWarnings("unchecked")
	public void connect(String ip, int port, SocketChannelInitializeHandler handler,
			Entry<ChannelOption, Object>... options) {

		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group);
			b.channel(NioSocketChannel.class);

			for (int i = 0; i < options.length; i++) {
				b.option(options[i].getKey(), options[i].getValue());
			}
			b.handler(handler);

			ChannelFuture f = b.connect(ip, port).sync();
			f.channel().closeFuture().sync();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}

	
	@SuppressWarnings("unchecked")
	public void bind(final int inetport, final SocketChannelInitializeHandler subHandler, int backlog,
			final Entry<ChannelOption, Object>... childOptions) {

		Entry[] options = { new KeyValuePair<ChannelOption<Integer>, Integer>(ChannelOption.SO_BACKLOG, backlog) };
		bind(inetport, subHandler, options, childOptions);
	}

	@SuppressWarnings("unchecked")
	public void bind(final int inetport, final SocketChannelInitializeHandler subHandler,
			final Entry<ChannelOption, Object>[] options, final Entry<ChannelOption, Object>... childOptions) {
		bind(inetport, new LoggingHandler(LogLevel.INFO), subHandler, options, childOptions);
	}
	
	@SuppressWarnings("unchecked")
	public void bind(final int inetport, final ChannelHandler handler,
			final SocketChannelInitializeHandler subHandler, final Entry<ChannelOption, Object>[] options,
			final Entry<ChannelOption, Object>... childOptions) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				EventLoopGroup bossGroup = new NioEventLoopGroup();
				EventLoopGroup workerGroup = new NioEventLoopGroup();
				try {
					ServerBootstrap sb = new ServerBootstrap();
					sb.group(bossGroup, workerGroup);
					sb.handler(handler);

					for (int i = 0; i < options.length; i++) {
						sb.option(options[i].getKey(), options[i].getValue());
					}

					sb.childHandler(subHandler);
					sb.channel(NioServerSocketChannel.class);
					for (int i = 0; i < childOptions.length; i++) {
						sb.childOption(childOptions[i].getKey(), childOptions[i].getValue());
					}
					ChannelFuture future = sb.bind(inetport).sync();
					log.debug(String
							.format("Listening port %d register by %s", inetport, subHandler.getClass().getName()));
					future.channel().closeFuture().sync();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					workerGroup.shutdownGracefully();
					bossGroup.shutdownGracefully();
				}
			}
		});

		thread.start();
	}
	
	

}
