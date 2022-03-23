package com.cobnet.connection;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

record ChannelAcceptor(NettyServer<?> server, NettyServer<?>.Builder builder) implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ChannelAcceptor.class);

    @Override
    public void run() {

        try {

            ServerBootstrap bootstrap = new ServerBootstrap();

            if(builder.getInetHost() != null && builder.getInetHost().length() > 0) {

                bootstrap.localAddress(builder.getInetHost(), builder.getInetPort());

            } else {

                bootstrap.localAddress(builder.getInetPort());
            }

            bootstrap.channel(builder.getServerChannel());

            bootstrap.group(builder.getGroup(), builder.getChildGroup());

            if (builder.getHandler() != null) {

                bootstrap.handler(builder.getHandler());
            }

            if (builder.getChildHandler() != null) {

                bootstrap.childHandler(builder.getChildHandler());
            }

            for (Map.Entry<ChannelOption<?>, ?> option : builder.getOptions()) {

                bootstrap.option((ChannelOption<Object>) option.getKey(), option.getValue());
            }

            for (Map.Entry<ChannelOption<?>, ?> option : builder.getChildOptions()) {

                bootstrap.childOption((ChannelOption<Object>) option.getKey(), option.getValue());
            }

            for (Map.Entry<AttributeKey<?>, ?> attribute : builder.getAttributes()) {

                bootstrap.attr((AttributeKey<Object>) attribute.getKey(), attribute.getValue());
            }

            for (Map.Entry<AttributeKey<?>, ?> attribute : builder.getChildAttributes()) {

                bootstrap.childAttr((AttributeKey<Object>) attribute.getKey(), attribute.getValue());
            }

            ChannelFuture future = bootstrap.bind().sync();

            this.server.setChannel(future.channel());
            this.server.getChannel().attr(NettyServer.SERVER_KEY).set(this.server);

            if (future.isDone() && future.isSuccess()) {

                if(builder.getInetHost() != null && builder.getInetHost().length() > 0) {

                    LOG.info(String.format("Listening address %s:%d registered by %s(%s)", builder.getInetHost(), builder.getInetPort(), this.server.getName(), builder.getChildHandler().getClass().getName()));
                } else {

                    LOG.info(String.format("Listening port %d registered by %s(%s)", builder.getInetPort(), this.server.getName(), builder.getChildHandler().getClass().getName()));
                }

                if (NettyServer.SERVERS.containsKey(this.server.getName())) {

                    NettyServer.SERVERS.get(this.server.getName()).close();
                }

                NettyServer.SERVERS.put(this.server.getName(), this.server);

                this.server.add(this.server.getChannel());
            }

            this.server.getChannel().closeFuture().sync();

        } catch (InterruptedException ex) {

            ex.printStackTrace();

        } finally {

            builder.getGroup().shutdownGracefully();
            builder.getChildGroup().shutdownGracefully();
        }
    }
}
