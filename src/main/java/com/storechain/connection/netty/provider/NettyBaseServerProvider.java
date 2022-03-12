package com.storechain.connection.netty.provider;

import java.lang.reflect.InvocationTargetException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.storechain.connection.netty.NettyServer;
import com.storechain.connection.netty.handler.ChannelInitializeHandler;
import com.storechain.interfaces.connection.NettyChannelBuilder;
import com.storechain.interfaces.connection.NettyServerProvider;
import com.storechain.spring.boot.configuration.NettyConfiguration.ServerConfiguration;

import io.netty.channel.ChannelHandler;

public class NettyBaseServerProvider implements NettyServerProvider {
	
	protected static final Logger log = LogManager.getLogger(NettyBaseServerProvider.class);

	@Override
	public void provide(ServerConfiguration config) {
		
		NettyServer server = new NettyServer(config.getName(), config.getPort());
		
		ChannelHandler handler;
		
		ChannelInitializeHandler<?> sub_handler;
		
		try {
			Class<?> handler_class = config.getHandler();
			Class<?> sub_handler_class = config.getSubHandler();
			Class<?> session_builder_class = config.getChannelBuilderr();
			
			if(sub_handler_class == null) {
				sub_handler_class = ChannelInitializeHandler.class;
			}
			
			NettyChannelBuilder<?, ?> builder = (NettyChannelBuilder<?, ?>) session_builder_class.getConstructor().newInstance();
			
			handler = (ChannelHandler) handler_class.getConstructor().newInstance();

			sub_handler = (ChannelInitializeHandler<?>) sub_handler_class.getConstructor(NettyServer.class, NettyChannelBuilder.class).newInstance(server, builder);

			server.bind(handler, sub_handler, config.getOptions(), config.getChildOptions());
			
		} catch(ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

}
