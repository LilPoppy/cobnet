package com.storechain.utils;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.Session;
import com.google.common.reflect.TypeToken;
import com.storechain.spring.boot.service.ManagedSessionRepository;

public class SessionManager {
	
	public static SessionRegistry getSessionRegistry() {
		
		return SpringContext.getSecurityConfiguration().getSessionRegistry();
	}

	@SuppressWarnings({ "unchecked", "serial" })
	public static <T extends Session> ManagedSessionRepository<T> getRepository(Class<? super T> session) {
		
		return (ManagedSessionRepository<T>) SpringContext.getContext().getAutowireCapableBeanFactory().getBean(new TypeToken<ManagedSessionRepository<T>>() {}.getRawType());
	}
	
	public static Set<String> getSessionIds() {
		
		String namespace = SpringContext.getSessionConfiguration().getNamespace();
		
		int length = namespace.split(":").length;
		
		return DatabaseManager.getRedisIndexedSessionRepository().getSessionRedisOperations().keys(namespace + ":sessions*").stream().filter(key -> key.toString().split(":").length == length + 2).map(key -> {
			
			String[] nodes = key.toString().split(":");
			
			return nodes[nodes.length - 1];
			
		}).collect(Collectors.toUnmodifiableSet());
	}
	
	

}
