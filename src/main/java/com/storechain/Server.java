package com.storechain;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import com.google.common.io.BaseEncoding;
import com.storechain.connection.InboundPacket;
import com.storechain.connection.OutboundPacket;
import com.storechain.connection.handler.WebSocketInitializeHandler;
import com.storechain.connection.netty.NettyChannelAdapter;
import com.storechain.connection.netty.NettyChannelHandlerPool;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

@SpringBootApplication
@EnableEurekaServer
public class Server {

	protected static Logger log = Logger.getLogger(Server.class);
	
	public static AnnotationConfigServletWebServerApplicationContext CONTEXT;
	
	public static final ChannelGroup CONNECTIONS = NettyChannelHandlerPool.CHANNEL_GROUP;
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		Server.CONTEXT = (AnnotationConfigServletWebServerApplicationContext) SpringApplication.run(Server.class, args);

		new NettyChannelAdapter().bind(8090, new WebSocketInitializeHandler(), 1024);
//		OutboundPacket out = new OutboundPacket(26);
//		System.out.println(out);
//		out.encodeUInt(429496729L);
//		System.out.println(out);
//		out.encodeUShort(65535);
//		System.out.println(out);
//		out.encodeString("测试一下：testing ！！");
//		
//		InboundPacket in = new InboundPacket(out.getData());
//		System.out.println(in);
//		System.out.println(String.format("header: %d", in.decodeInt()));
//		System.out.println(in);
//		System.out.println(String.format("value: %d", in.decodeUInt()));
//		System.out.println(in);
//		System.out.println(String.format("value: %d", in.decodeUShort()));
//		System.out.println(in);
//		System.out.println(String.format("string: %s", in.decodeString()));
//		System.out.println(in);
//	   byte[] record = "你好！".getBytes();
//	   ByteBuffer bb = ByteBuffer.wrap(record);
//	   bb.order(ByteOrder.BIG_ENDIAN);
//	   System.out.println(BaseEncoding.base16().encode(bb.array()));
			
	}

}

