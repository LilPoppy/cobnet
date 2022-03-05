package com.storechain.utils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.storechain.spring.boot.configuration.NettyConfiguration;
import com.storechain.spring.boot.configuration.PersistenceConfiguration;
import com.storechain.spring.boot.configuration.ProjectInformationConfiguration;
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
	
	private static ProjectInformationConfiguration PROJECT_INFOMATION_CONFIGURATION;
	
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
	
	public static ProjectInformationConfiguration getProjectInformationConfiguration() {
		return PROJECT_INFOMATION_CONFIGURATION;
	}
	
	
	public static ServletRequest getCurrentRequest() {
		
	    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
	    
	    if (attributes instanceof ServletRequestAttributes) {
	    	
	        return ((ServletRequestAttributes)attributes).getRequest();
	    }
	    
	    return null;
	}
	
	public static ServletResponse getCurrentResponse() {
		
	    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
	    
	    if (attributes instanceof ServletRequestAttributes) {
	    	
	    	return ((ServletRequestAttributes)attributes).getResponse();
	    }
	    
	    return null;
	}
	
    public static Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
		
    	ExpressionParser parser = new SpelExpressionParser();
    	
		StandardEvaluationContext context = new StandardEvaluationContext();
		
		for (int i = 0; i < parameterNames.length; i++) {
			
			context.setVariable(parameterNames[i], args[i]);
		}
		
		return parser.parseExpression(key).getValue(context, Object.class);
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
		
		@Autowired
		public void setProjectInformationConfiguration(ProjectInformationConfiguration config) {
			SpringContext.PROJECT_INFOMATION_CONFIGURATION = config;
		}
		
	}
}
