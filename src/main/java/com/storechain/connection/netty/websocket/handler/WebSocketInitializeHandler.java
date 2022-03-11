package com.storechain.connection.netty.websocket.handler;


import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLHandshakeException;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.util.ResourceUtils;

import com.storechain.EntryPoint;
import com.storechain.connection.InboundPacket;
import com.storechain.connection.InboundPacketBuilder;
import com.storechain.connection.netty.handler.ChannelInitializeHandler;
import com.storechain.connection.netty.websocket.WebSocketSessionBuilder;
import com.storechain.connection.netty.websocket.WebSocketServer;
import com.storechain.utils.SpringContext;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketInitializeHandler extends ChannelInitializeHandler<NioSocketChannel> {
	
	private final WebSocketServer server;

	public WebSocketInitializeHandler(WebSocketServer server) throws NoSuchMethodException, SecurityException {
		super(server, new WebSocketSessionBuilder());
		this.server = server;
	}

	@Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ChannelPipeline pipe = ch.pipeline();

        pipe.addLast(new HttpServerCodec());
        pipe.addLast(new ChunkedWriteHandler());
        pipe.addLast(new HttpObjectAggregator(65535));
        pipe.addLast(new WebSocketInboundUrlHandler());
        pipe.addLast(new WebSocketServerProtocolHandler("/connect"));
        pipe.addLast(new WebSocketInboundBinaryFrameHandler<InboundPacket>(server, new InboundPacketBuilder()));
        pipe.addLast(new WebSocketActivityHandler(this.server));
        pipe.addLast(new WebSocketInboundPacketHandler(this.server));
        
		ConfigurableListableBeanFactory beans = SpringContext.getContext().getBeanFactory();
		
		TomcatServletWebServerFactory tomcat = beans.getBean(beans.getBeanNamesForType(TomcatServletWebServerFactory.class)[0], TomcatServletWebServerFactory.class);
        
		Ssl ssl = tomcat.getSsl();
		
        KeyStore store = KeyStore.getInstance(ssl.getKeyStoreType());
        
        KeyManagerFactory factory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        
        SSLContext context = SSLContext.getInstance(ssl.getProtocol());
        
		try {
			
			URL url = ResourceUtils.getURL(ssl.getKeyStore());
			
			try (InputStream stream = url.openStream()) {
				store.load(stream, ssl.getKeyStorePassword().toCharArray());
			}

		}
		catch (Exception ex) {
			throw new WebServerException("Could not load key store '" + ssl.getKeyStore() + "'", ex);
		}
        
		factory.init(store, ssl.getKeyStorePassword().toCharArray());
		context.init(factory.getKeyManagers(), null, null);
		
        SSLEngine engine = context.createSSLEngine();
        engine.setUseClientMode(false);
        engine.setNeedClientAuth(false);
        
        pipe.addFirst("ssl", new WebSocketSslHandler(engine));
        
        super.initChannel(ch);
    }
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		
		ctx.close();

		if(cause instanceof SSLHandshakeException) {
			log.warn(String.format("%s has initialize without successed.", ctx.channel().remoteAddress()));
			return;
		}
		
		super.exceptionCaught(ctx, cause);
		
	}
   
}
