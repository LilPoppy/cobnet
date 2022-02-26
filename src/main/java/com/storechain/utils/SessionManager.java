package com.storechain.utils;

import org.springframework.session.Session;
import com.google.common.reflect.TypeToken;
import com.storechain.spring.boot.service.SimpleSessionRepository;

public class SessionManager {

	@SuppressWarnings({ "unchecked", "serial" })
	public static <T extends Session> SimpleSessionRepository<T> getRepository(Class<? super T> session) {
		
		return (SimpleSessionRepository<T>) SpringManager.getContext().getAutowireCapableBeanFactory().getBean(new TypeToken<SimpleSessionRepository<T>>() {}.getRawType());
	}

}
