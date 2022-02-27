package com.storechain.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.storechain.spring.boot.configuration.NettyConfiguration;
import com.storechain.spring.boot.configuration.PersistenceConfiguration;
import com.storechain.spring.boot.configuration.RedisConfiguration;
import com.storechain.spring.boot.configuration.SecurityConfiguration;
import com.storechain.spring.boot.configuration.SessionConfiguration;
import com.storechain.spring.boot.configuration.SystemConfiguration;

@Component
public final class SpringContext {
	
	private static ServletWebServerApplicationContext CONTEXT;
	
	private static NettyConfiguration NETTY_CONFIG;
	
	private static SystemConfiguration SYSTEM_CONFIG;
	
	private static RedisConfiguration REDIS_CONFIG;
	
	private static SessionConfiguration SESSION_CONFIG;
	
	private static SecurityConfiguration SECURITY_CONFIG;
	
	private static PersistenceConfiguration PERSISTENCE_CONFIGURATION;
	
	private SpringContext() { }
	
	public static ServletWebServerApplicationContext getContext() {
		
		return SpringContext.CONTEXT;
	}
	
	public static NettyConfiguration getNettyConfiguration() {
		return NETTY_CONFIG;
	}

	public static SystemConfiguration getSystemConfiguration() {
		return SYSTEM_CONFIG;
	}

	public static RedisConfiguration getRedisConfiguration() {
		return REDIS_CONFIG;
	}

	public static SessionConfiguration getSessionConfiguration() {
		return SESSION_CONFIG;
	}

	public static SecurityConfiguration getSecurityConfiguration() {
		return SECURITY_CONFIG;
	}

	public static PersistenceConfiguration getPersistenceConfiguration() {
		return PERSISTENCE_CONFIGURATION;
	}

	@Component
	final static class SpringManagerBean {
		
		@Autowired
		public void setContext(ServletWebServerApplicationContext context) {
			SpringContext.CONTEXT = context;
		}
		
		@Autowired
		public void setNettyConfiguration(NettyConfiguration config) {
			SpringContext.NETTY_CONFIG = config;
		}
		
		@Autowired
		public void setSystemConfiguration(SystemConfiguration config) {
			SpringContext.SYSTEM_CONFIG = config;
		}
		
		@Autowired
		public void setRedisConfiguration(RedisConfiguration config) {
			SpringContext.REDIS_CONFIG = config;
		}
		
		
		@Autowired
		public void setSessionConfiguration(SessionConfiguration config) {
			SpringContext.SESSION_CONFIG = config;
		}
		
		@Autowired
		public void setSecurityConfiguration(SecurityConfiguration config) {
			SpringContext.SECURITY_CONFIG = config;
		}
		
		@Autowired
		public void setPersistenceConfiguration(PersistenceConfiguration config) {
			SpringContext.PERSISTENCE_CONFIGURATION = config;
		}
		
	}
}
