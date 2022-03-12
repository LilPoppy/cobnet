package com.storechain.connection.netty.provider;

import java.lang.reflect.InvocationTargetException;
import java.util.Map.Entry;

import com.storechain.connection.netty.handler.ChannelInitializeHandler;
import com.storechain.connection.netty.websocket.WebSocketChannel;
import com.storechain.connection.netty.websocket.WebSocketServer;
import com.storechain.connection.netty.websocket.handler.WebSocketChannelInitializeHandler;
import com.storechain.interfaces.connection.NettyServerProvider;
import com.storechain.spring.boot.configuration.NettyConfiguration.ServerConfiguration;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;

public class WebSocketServerProvider implements NettyServerProvider {

	@Override
	public void provide(ServerConfiguration config) {
		
		WebSocketServer server = new WebSocketServer(config.getName(), config.getPort());
		
		ChannelHandler handler;
		
		ChannelInitializeHandler<?> sub_handler;

		try {
			
			Class<?> handler_class = config.getHandler();
			
			Class<?> sub_handler_class = config.getSubHandler();

			handler = (ChannelHandler) handler_class.getConstructor().newInstance();
			
			if(sub_handler_class == null) {
				
				sub_handler = new WebSocketChannelInitializeHandler(server);
			} else {
				
				sub_handler = (ChannelInitializeHandler<?>) sub_handler_class.getConstructor(WebSocketServer.class).newInstance(server);
			}
			
			server.bind(handler, sub_handler, config.getOptions(), config.getChildOptions());
			
		}catch(ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
