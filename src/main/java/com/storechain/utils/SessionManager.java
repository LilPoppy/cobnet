package com.storechain.utils;

import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.Session;
import com.google.common.reflect.TypeToken;
import com.storechain.spring.boot.service.SimpleSessionRepository;

public class SessionManager {
	
	public static SessionRegistry getSessionRegistry() {
		
		return SpringContext.getSecurityConfiguration().getSessionRegistry();
	}

	@SuppressWarnings({ "unchecked", "serial" })
	public static <T extends Session> SimpleSessionRepository<T> getRepository(Class<? super T> session) {
		
		return (SimpleSessionRepository<T>) SpringContext.getContext().getAutowireCapableBeanFactory().getBean(new TypeToken<SimpleSessionRepository<T>>() {}.getRawType());
	}

}
