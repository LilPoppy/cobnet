package com.storechain.connection.netty;


import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;


import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import com.storechain.EntryPoint;
import com.storechain.common.KeyValuePair;
import com.storechain.connection.InboundPacket;
import com.storechain.connection.netty.handler.ChannelInitializeHandler;
import com.storechain.interfaces.connection.ConnectionListener;
import com.storechain.interfaces.connection.annotation.ConnectionHandler;
import com.storechain.spring.boot.configuration.NettyConfiguration.ServerConfiguration;
import com.storechain.utils.SpringContext;
import com.storechain.utils.TaskProvider;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import org.slf4j.Logger;

//TODO serialize server cache to SQL or something else and reload it when server starts again.
public class NettyServer extends DefaultChannelGroup {
	
	public static final AttributeKey<NettyServer> SERVER_KEY = AttributeKey.valueOf("StoreChain-Server");
	
	public static final List<NettyServer> SERVERS = new ArrayList<NettyServer>();
	
	private final String name;
	
	protected static Logger log = LoggerFactory.getLogger(NettyServer.class);
	
	private volatile NioServerSocketChannel channel;
	
	private final int port;
	
	private final Map<ConnectionListener, ConnectionHandler> handlers = new HashMap<ConnectionListener,ConnectionHandler>();
	
	private final Set<InetAddress> blocks = new HashSet<InetAddress>();
	
	private final Map<InetAddress, ScheduledFuture<?>> blockTimers = new HashMap<InetAddress, ScheduledFuture<?>>();
	
	private final ServerConfiguration config;
	
	public NettyServer(String name, int port) {
		super(GlobalEventExecutor.INSTANCE);
		this.name = name;
		this.channel = channel;
		this.port = port;
		this.config = SpringContext.getNettyConfiguration().getServerConfig(this);
	}
	
	public static NettyServer getInstance(String name) {
		return SERVERS.stream().filter(server -> server.name.toUpperCase().equals(name.toUpperCase())).findFirst().orElse(null);
	}
	
	public List<ConnectionListener> getListeners() {
		return Collections.unmodifiableList(handlers.keySet().stream().toList());
	}
	
	public ConnectionHandler getHandler(ConnectionListener listener) {
		return handlers.get(listener);
	}
	
	public void addListener(ConnectionListener listener) throws ClassNotFoundException {
		
		try {
			ConnectionHandler handler = listener.getClass().getMethod("onEvent", NettyClient.class, InboundPacket.class).getAnnotation(ConnectionHandler.class);
			
			if(handler == null) {
				throw new ClassNotFoundException("Cannot find annotation of @Handler in onEvent methond.");
			}
			
			handlers.put(listener, handler);
			
		} catch(NoSuchMethodException|SecurityException ex) {
			ex.printStackTrace();
		} 
	}
	
	public void removeListener(ConnectionListener listener) {
		handlers.remove(listener);
	}
	
	public int getPort() {
		return this.port;
	}
	
	public NioServerSocketChannel getChannel() {
		return this.channel;
	}
	
	@Async
	@SuppressWarnings("unchecked")
	public void bind(final ChannelInitializeHandler subHandler, int backlog,
			final Entry<ChannelOption, Object>... childOptions) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Entry[] options = { new KeyValuePair<ChannelOption<Integer>, Integer>(ChannelOption.SO_BACKLOG, backlog) };
		bind(subHandler, options, childOptions);
	}
	
	@Async
	@SuppressWarnings("unchecked")
	public void bind(final ChannelInitializeHandler subHandler, final Entry<ChannelOption, Object>[] options, final Entry<ChannelOption, Object>... childOptions) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		bind(new LoggingHandler(LogLevel.INFO), subHandler, options, childOptions);
	}
	
	
	@Async
	@SuppressWarnings("unchecked")
	public void bind(final ChannelHandler handler, final ChannelInitializeHandler subHandler, final Entry<ChannelOption, Object>[] objects,
			final Entry<ChannelOption, Object>... object) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		NettyServer server = this;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				EventLoopGroup bossGroup = new NioEventLoopGroup();
				EventLoopGroup workerGroup = new NioEventLoopGroup();
				try {
					ServerBootstrap sb = new ServerBootstrap();
					sb.group(bossGroup, workerGroup);
					sb.handler(handler);

					for (int i = 0; i < objects.length; i++) {
						sb.option(objects[i].getKey(), objects[i].getValue());
					}

					sb.childHandler(subHandler);
					sb.channel(NioServerSocketChannel.class);
					
					for (int i = 0; i < object.length; i++) {
						sb.childOption(object[i].getKey(), object[i].getValue());
					}
					
					ChannelFuture future = sb.bind(port);
					
					channel = (NioServerSocketChannel) future.channel();
					channel.attr(NettyServer.SERVER_KEY).set(server);

					future = future.sync();
					
					if(future.isDone() && future.isSuccess()) {
						
						log.info(String.format("Listening port %d registed by %s(%s)", port, getName(), subHandler.getClass().getName()));

						SERVERS.add(server);
					}
					
					channel.closeFuture().sync();

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
	
	public List<NettyClient> getConnections() {
		return Collections.unmodifiableList(this.stream().map(channel -> channel.attr(NettyClient.CLIENT_KEY).get()).toList()); 
	}
	
	@SuppressWarnings("rawtypes")
	public boolean add(NettyClient client) {
		return this.add(client.channel);
	}
	
	@SuppressWarnings("rawtypes")
	public boolean remove(NettyClient client) {
		return this.remove(client.channel);
	}
	
	public synchronized List<InetAddress> getBlackList() {
		return Collections.unmodifiableList(this.blocks.stream().toList());
	}
	
	public synchronized boolean isBlocked(InetAddress address) {
		return this.blocks.contains(address);
	}
	
	public synchronized void block(NettyClient client) {
		this.block(client.getAddress().getAddress());
	}
	
	public synchronized void block(InetAddress address) {
		this.blocks.add(address);
		
		ScheduledFuture<?> future = this.blockTimers.get(address);
		
		if(future != null) {
			future.cancel(false);
			this.blockTimers.remove(address);
		}
	}
	
	
	public synchronized void block(NettyClient client, Date date) {
		this.block(client.getAddress().getAddress(), date);
	}
	

	public synchronized void block(InetAddress address, Date date) {
		
		synchronized(blocks) {
			
			this.block(address);
			
			
			this.blockTimers.put(address, TaskProvider.schedule(new Runnable() 
			{

				@Override
				public void run() {
					unblock(address);
				}
				
			}, date));
			
			
			TaskProvider.purge();
		}

	}
	
	public synchronized boolean unblock(InetAddress address) {
		
		ScheduledFuture<?> future = this.blockTimers.get(address);
		
		if(future != null) {
			future.cancel(false);
			this.blockTimers.remove(address);
		}
		
		return this.blocks.remove(address);
	}
	
	public synchronized boolean unblock(NettyClient client) {
		return this.unblock(client.getAddress().getAddress());
	}

	public String getName() {
		return name;
	}

	public ServerConfiguration getConfiguration() {
		return config;
	}

}
