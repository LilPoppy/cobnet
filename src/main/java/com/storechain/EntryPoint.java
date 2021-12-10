package com.storechain;

import java.lang.reflect.InvocationTargetException;
import javax.script.ScriptEngineManager;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import com.storechain.interfaces.spring.connection.NettyServerProvider;
import com.storechain.spring.boot.configuration.NettyConfiguration;
import com.storechain.spring.boot.configuration.SystemConfiguration;

@SpringBootApplication
//@EnableEurekaClient
public class EntryPoint {

	protected static Logger log = Logger.getLogger(EntryPoint.class);
	
	public static ServletWebServerApplicationContext CONTEXT;
	
	public static NettyConfiguration NETTY_CONFIG;
	
	public static SystemConfiguration SYSTEM_CONFIG;

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {

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

		System.out.println(sem.getEngineByName("graal.js"));
		System.out.println(sem.getEngineFactories().size());
		for(var se : sem.getEngineFactories()) {
			System.out.println(se.getEngineName());
		}
	}
	
}

