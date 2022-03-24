package com.cobnet.connection.websocket.handler;

import com.cobnet.connection.support.handler.ChannelInitializerHandler;
import com.cobnet.connection.websocket.WebSocketChannel;
import com.cobnet.connection.websocket.WebSocketChannelProvider;
import com.cobnet.connection.websocket.WebSocketServer;
import com.cobnet.interfaces.connection.ChannelProvider;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.InputStream;
import java.net.URL;
import java.security.*;

public class WebSocketChannelInitializerHandler extends ChannelInitializerHandler<WebSocketServer, WebSocketChannel> {

    public WebSocketChannelInitializerHandler(WebSocketServer server) {

        super(server);
    }

    @Override
    protected void beforeInitChannel(WebSocketChannel channel) {

        ChannelPipeline pipe = channel.pipeline();

        pipe.addLast(new HttpServerCodec());
        pipe.addLast(new ChunkedWriteHandler());
        pipe.addLast(new HttpObjectAggregator(65535));
        pipe.addLast(new WebSocketInboundUrlResolvingHandler(this.getServer()));
        pipe.addLast(new WebSocketServerProtocolHandler("/" + this.getServer().getName()));
        pipe.addLast(new WebSocketFrameInboundHandler());
    }

    @Override
    protected void afterInitChannel(WebSocketChannel channel) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {

        //TODO: specific path way to import ssl certificate

        ConfigurableListableBeanFactory beans = ProjectBeanHolder.getSpringContext().getBeanFactory();

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

        if(ssl != null) {

            channel.pipeline().addFirst("ssl", new WebSocketSslHandler(engine));
        }
    }

    @Override
    protected ChannelProvider<WebSocketServer, WebSocketChannel> getProvider() {

        return new WebSocketChannelProvider();
    }
}
