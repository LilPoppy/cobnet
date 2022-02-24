package com.storechain.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

import com.storechain.EntryPoint;
import com.storechain.spring.boot.service.SessionRepositoryService;

@Component
public class SessionManager {

	@Autowired
	private SessionRepositoryService<? extends Session> repository;
	
	public static SessionRepositoryService<? extends Session> getRepository() {
		
		return EntryPoint.SESSION_MANAGER.repository;
	}

}
