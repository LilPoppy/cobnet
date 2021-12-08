package com.storechain;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import com.storechain.connection.handler.UnknownPacketHandler;
import com.storechain.connection.netty.NettyServer;
import com.storechain.connection.netty.websocket.WebSocketServer;
import com.storechain.interfaces.spring.connection.NettyServerProvider;
import com.storechain.spring.boot.configuration.NettyConfiguration;
import com.storechain.spring.boot.configuration.SystemConfiguration;

@SpringBootApplication
@EnableEurekaServer
public class EntryPoint {

	protected static Logger log = Logger.getLogger(EntryPoint.class);
	
	public static AnnotationConfigServletWebServerApplicationContext CONTEXT;
	
	public static NettyConfiguration NETTY_CONFIG;
	
	public static SystemConfiguration SYSTEM_CONFIG;
	

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
		
		EntryPoint.CONTEXT = (AnnotationConfigServletWebServerApplicationContext) SpringApplication.run(EntryPoint.class, args);
		EntryPoint.NETTY_CONFIG = EntryPoint.CONTEXT.getBean(NettyConfiguration.class);
		EntryPoint.SYSTEM_CONFIG = EntryPoint.CONTEXT.getBean(SystemConfiguration.class);
		
		if(NETTY_CONFIG.isEnable() && NETTY_CONFIG.getServers() != null) {
			
			for(NettyConfiguration.ServerConfiguration serverConfig : NETTY_CONFIG.getServers()) {
				
				Class<?> provider_class = serverConfig.getProvider();
				
				NettyServerProvider provider = (NettyServerProvider) provider_class.getConstructor().newInstance();

				provider.providing(serverConfig);
			}
		}
	}
	
}

