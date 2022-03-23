package com.cobnet.connection.handler;

import com.cobnet.connection.NettyChannel;
import com.cobnet.connection.NettyServer;
import com.cobnet.interfaces.connection.ChannelProvider;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ipfilter.RuleBasedIpFilter;

@ChannelHandler.Sharable
public abstract class ChannelInitializeHandler<T extends NettyServer<E>, E extends NettyChannel> extends ChannelInitializer<NioSocketChannel> {

    private final T server;

    public ChannelInitializeHandler(T server) {

        this.server = server;
    }

    @Override
    protected void initChannel(NioSocketChannel channel) throws Exception {

        channel.attr(NettyChannel.CHANNEL_KEY).set(this.getProvider().provide(this.server, channel));

        ChannelPipeline pipe = channel.pipeline();

        pipe.addLast(new ChannelPacketDecoder(), new ChannelInboundPacketHandler());
        pipe.addFirst(new RuleBasedIpFilter(new IpFilterRuleHandler<T>(this.server)));
    }

    protected abstract ChannelProvider<T, E> getProvider();
}
