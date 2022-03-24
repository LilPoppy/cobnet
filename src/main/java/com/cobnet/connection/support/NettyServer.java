package com.cobnet.connection.support;

import com.cobnet.common.Endian;
import com.cobnet.common.KeyValuePair;
import com.cobnet.connection.support.handler.ChannelActivityHandler;
import com.cobnet.connection.support.handler.ChannelInitializerHandler;
import com.cobnet.connection.support.handler.ChannelPacketInboundHandler;
import com.cobnet.interfaces.connection.ServerInitializer;
import com.cobnet.interfaces.connection.TransmissionDecoder;
import com.cobnet.interfaces.connection.TransmissionEncoder;
import com.cobnet.interfaces.connection.TransmissionInboundHandler;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

//TODO channel的集合；
public abstract class NettyServer<T extends NettyChannel> extends DefaultChannelGroup {

    private final static Logger LOG = LoggerFactory.getLogger(NettyServer.class);

    static final Map<String, NettyServer<?>> SERVERS = new HashMap<>();

    public static final AttributeKey<NettyServer<?>> SERVER_KEY = AttributeKey.valueOf("cobnet-server");

    private final String name;

    private volatile Channel channel;

    private Endian endian;

    private Charset charset;

    private Supplier<TransmissionEncoder<T, ?>> encoder;

    private Supplier<TransmissionDecoder<T, ?>> decoder;

    private TransmissionInboundHandler<?> inboundHandler;

    private ChannelActivityHandler<? extends NettyServer<T>, T> activityHandler;

    protected abstract Builder build(Builder builder);

    protected NettyServer(String name) {

        super(GlobalEventExecutor.INSTANCE);

        this.name = name;
        this.endian = Endian.LITTLE;
        this.charset = StandardCharsets.UTF_8;
    }

    public String getName() {

        return this.name;
    }

    public Endian getEndian() {

        return endian;
    }

    public Charset getCharset() {

        return charset;
    }

    public Supplier<TransmissionEncoder<T, ?>> getEncoder() {

        return encoder;
    }

    public Supplier<TransmissionDecoder<T, ?>> getDecoder() {

        return decoder;
    }

    public TransmissionInboundHandler<?> getInboundHandler() {

        return inboundHandler;
    }

    public ChannelActivityHandler<? extends NettyServer<T>, T> getActivityHandler() {

        return activityHandler == null ? new NettyServerChannelActivityHandler<>(this) : activityHandler;
    }

    public void setEndian(Endian endian) {

        this.endian = endian;
    }

    public void setCharset(Charset charset) {

        this.charset = charset;
    }

    public int getPort() {

        if(this.channel.localAddress() instanceof InetSocketAddress address) {

            return address.getPort();
        }

        return 0;
    }

    public Channel getChannel() {

        return this.channel;
    }

    void setChannel(Channel channel) {

        this.channel = channel;
    }

    protected void bind(Builder builder) {

        ChannelAcceptor acceptor = new ChannelAcceptor(this, builder);
        this.encoder = builder.encoder;
        this.decoder = builder.decoder;
        this.inboundHandler = builder.inboundHandler;
        this.activityHandler = builder.activityHandler;
        Thread thread = new Thread(acceptor);
        thread.setName(this.getName());
        thread.start();
    }

    public NettyServer<T> bind(int port) {

        this.bind(build(new Builder().setInetPort(port)));

        return this;
    }

    public NettyServer<T> bind(String host, int port) {

        this.bind(build(new Builder().setInetHost(host).setInetPort(port)));

        return this;
    }

    @Override
    public ChannelGroupFuture close(ChannelMatcher matcher) {

        if(matcher.matches(this.channel)) {

            NettyServer.SERVERS.remove(this.getName());
        }

        return super.close(matcher);
    }

    public static NettyServer<?> getInstance(String name) {

        return NettyServer.SERVERS.get(name);
    }

    public static String[] getServerNames() {

        return NettyServer.SERVERS.keySet().toArray(String[]::new);
    }

    protected final class Builder {

        private String inetHost;

        private int inetPort;

        private Class<? extends ServerChannel> serverChannel = NioServerSocketChannel.class;

        private EventLoopGroup group = new NioEventLoopGroup();

        private EventLoopGroup childGroup = new NioEventLoopGroup();

        private ServerInitializer<? extends NettyServer<T>> handler;

        private ChannelInitializerHandler<? extends NettyServer<T>, T> childHandler;

        private Map.Entry<ChannelOption<?>, ?>[] options = new KeyValuePair[]{};

        private Map.Entry<ChannelOption<?>, ?>[] childOptions = new KeyValuePair[]{};;

        private Map.Entry<AttributeKey<?>, ?>[] attributes = new KeyValuePair[]{};

        private Map.Entry<AttributeKey<?>, ?>[] childAttributes = new KeyValuePair[]{};;

        private Supplier<TransmissionEncoder<T, ?>> encoder;

        private Supplier<TransmissionDecoder<T, ?>> decoder;

        private TransmissionInboundHandler<?> inboundHandler = new ChannelPacketInboundHandler();

        private ChannelActivityHandler<? extends NettyServer<T>, T> activityHandler = null;

        Builder() {}

        public Builder setInetHost(String inetHost) {

            this.inetHost = inetHost;

            return this;
        }

        public Builder setInetPort(int inetPort) {

            this.inetPort = inetPort;

            return this;
        }

        public Builder setServerChannel(Class<? extends ServerChannel> channel) {

            this.serverChannel = channel;

            return this;
        }

        public Builder setGroup(EventLoopGroup group) {

            this.group = group;

            return this;
        }

        public Builder setChildGroup(EventLoopGroup group) {

            this.childGroup = group;

            return this;
        }

        public Builder setGroups(EventLoopGroup group, EventLoopGroup childGroup) {

            this.group = group;
            this.childGroup = childGroup;

            return this;
        }

        public <E extends ServerInitializer<? extends NettyServer<T>>> Builder setHandler(E handler) {

            this.handler = handler;

            return this;
        }

        public Builder setChildHandler(ChannelInitializerHandler<? extends NettyServer<T>, T> childHandler) {

            this.childHandler = childHandler;

            return this;
        }

        @SafeVarargs
        public final Builder setOptions(Map.Entry<ChannelOption<?>, ?>... options) {

            this.options = options;

            return this;
        }

        @SafeVarargs
        public final Builder setChildOptions(Map.Entry<ChannelOption<?>, ?>... childOptions) {

            this.childOptions = childOptions;

            return this;
        }

        @SafeVarargs
        public final Builder setAttributes(Map.Entry<AttributeKey<?>, ?>... attributes) {

            this.attributes = attributes;

            return this;
        }

        @SafeVarargs
        public final Builder setChildAttributes(Map.Entry<AttributeKey<?>, ?>... childAttributes) {

            this.childAttributes = childAttributes;

            return this;
        }

        public Builder setEncoder(Supplier<TransmissionEncoder<T, ?>> encoder) {

            this.encoder = encoder;

            return this;
        }

        public Builder setDecoder(Supplier<TransmissionDecoder<T, ?>> decoder) {

            this.decoder = decoder;

            return this;
        }

        public Builder setInboundHandler(TransmissionInboundHandler<?> inboundHandler) {

            this.inboundHandler = inboundHandler;

            return this;
        }

        public Builder setActivityHandler(ChannelActivityHandler<? extends NettyServer<T>, T> activityHandler) {

            this.activityHandler = activityHandler;

            return this;
        }

        public String getInetHost() {

            return inetHost;
        }

        public int getInetPort() {

            return inetPort;
        }

        public Class<? extends ServerChannel> getServerChannel() {

            return serverChannel;
        }

        public EventLoopGroup getGroup() {

            return group;
        }

        public EventLoopGroup getChildGroup() {

            return childGroup;
        }

        public ServerInitializer<? extends NettyServer<T>> getHandler() {

            return handler;
        }

        public ChannelInitializerHandler<? extends NettyServer<T>, T> getChildHandler() {

            return childHandler;
        }

        public Map.Entry<ChannelOption<?>, ?>[] getOptions() {

            return options;
        }

        public Map.Entry<ChannelOption<?>, ?>[] getChildOptions() {

            return childOptions;
        }

        public Map.Entry<AttributeKey<?>, ?>[] getAttributes() {

            return attributes;
        }

        public Map.Entry<AttributeKey<?>, ?>[] getChildAttributes() {

            return childAttributes;
        }

        public Supplier<TransmissionEncoder<T, ?>> getEncoder() {

            return encoder;
        }

        public Supplier<TransmissionDecoder<T, ?>> getDecoder() {

            return decoder;
        }

        public TransmissionInboundHandler<?> getInboundHandler() {

            return inboundHandler;
        }

        public ChannelActivityHandler<? extends NettyServer<T>, T> getActivityHandler() {

            return activityHandler;
        }
    }

    private static class NettyServerChannelActivityHandler<T extends NettyChannel> extends ChannelActivityHandler<NettyServer<T>, T> {

        public NettyServerChannelActivityHandler(NettyServer<T> server) {
            super(server);
        }

        @Override
        protected void channelActive(T channel) {}

        @Override
        protected void channelInactive(T channel) {}
    }
}
