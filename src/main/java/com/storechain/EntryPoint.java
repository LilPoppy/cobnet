package com.storechain;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.logging.logback.ColorConverter;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.nativex.hint.InitializationHint;

import com.storechain.interfaces.spring.connection.NettyServerProvider;
import com.storechain.spring.boot.configuration.NettyConfiguration;
import com.storechain.spring.boot.configuration.SystemConfiguration;

//@EnableEurekaClient
@SpringBootApplication(proxyBeanMethods = false)
@InitializationHint(types = ColorConverter.class, initTime = org.springframework.nativex.hint.InitializationTime.BUILD)
public class EntryPoint {

	private static Logger log = LoggerFactory.getLogger(EntryPoint.class);
	
	public static ServletWebServerApplicationContext CONTEXT;
	
	public static NettyConfiguration NETTY_CONFIG;
	
	public static SystemConfiguration SYSTEM_CONFIG;

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, IOException {

		EntryPoint.CONTEXT = (ServletWebServerApplicationContext) SpringApplication.run(EntryPoint.class, args);
		EntryPoint.NETTY_CONFIG = EntryPoint.CONTEXT.getBean(NettyConfiguration.class);
		EntryPoint.SYSTEM_CONFIG = EntryPoint.CONTEXT.getBean(SystemConfiguration.class);
		
		if(NETTY_CONFIG.isEnable() && NETTY_CONFIG.getServers() != null) {
			
			for(NettyConfiguration.ServerConfiguration serverConfig : NETTY_CONFIG.getServers()) {

				Class<?> provider_class = serverConfig.getProvider();
				
				NettyServerProvider provider = (NettyServerProvider) provider_class.getConstructor().newInstance();

				provider.providing(serverConfig);
			}
		}

		ScriptEngineManager sem = new ScriptEngineManager();

		for(var se : sem.getEngineFactories()) {
			log.info(se.getEngineName());
		}
	}
	
}

