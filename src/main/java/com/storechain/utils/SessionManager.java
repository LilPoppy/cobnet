package com.storechain.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.Session;

import com.storechain.spring.boot.service.SessionRepositoryService;

public class SessionManager {
	
	private static SessionManager INSTANCE;
	
	@Autowired
	private SessionRepositoryService<? extends Session> repository;

	@Autowired
	public static void setInstance(SessionManager iNSTANCE) {
		INSTANCE = iNSTANCE;
	}
	
	public static SessionRepositoryService<? extends Session> getRepository() {
		
		return INSTANCE.repository;
	}

}
