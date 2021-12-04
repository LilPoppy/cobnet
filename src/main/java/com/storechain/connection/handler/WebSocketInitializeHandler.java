package com.storechain.connection.handler;


import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.util.ResourceUtils;

import com.storechain.Server;
import com.storechain.connection.InboundPacket;
import com.storechain.connection.OutboundPacket;
import com.storechain.connection.netty.WebSocketClient;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketInitializeHandler extends SocketChannelInitializeHandler<NioSocketChannel, InboundPacket, WebSocketClient> {
	

	public WebSocketInitializeHandler() throws NoSuchMethodException, SecurityException {
		super(WebSocketClient.class.getConstructor(NioSocketChannel.class));
	}

	@Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        super.initChannel(ch);
        ChannelPipeline pipe = ch.pipeline();

        pipe.addLast(new HttpServerCodec());
        pipe.addLast(new ChunkedWriteHandler());
        pipe.addLast(new HttpObjectAggregator(65535));
        pipe.addLast(new WebSocketInboundUrlHandler());
        pipe.addLast(new WebSocketServerProtocolHandler("/connect"));
        pipe.addLast(new WebSocketInboundBinaryFrameHandler<InboundPacket>(InboundPacket.class.getConstructor(byte[].class)));
        pipe.addLast(new WebSocketHandler());
        
		ConfigurableListableBeanFactory beans = Server.CONTEXT.getBeanFactory();
		
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
    }
   
}
