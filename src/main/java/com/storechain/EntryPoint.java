package com.storechain;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import com.storechain.interfaces.spring.connection.NettyServerProvider;
import com.storechain.spring.boot.configuration.NettyConfiguration;
import com.storechain.spring.boot.configuration.SystemConfiguration;

//@EnableEurekaClient
@SpringBootApplication(proxyBeanMethods = false)
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
		
		sem.getEngineByName("Graal.js");
		for(var se : sem.getEngineFactories()) {
			log.info(se.getEngineName());
		}
		
		ScriptEngine engine = sem.getEngineByName("Graal.js");

		
	}
	
}

