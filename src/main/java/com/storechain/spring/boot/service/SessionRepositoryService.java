package com.storechain.spring.boot.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

@Service
public class SessionRepositoryService<T extends Session> {
	
	@Autowired
	private FindByIndexNameSessionRepository<T> repository;
	
	public T createSession() {
		
		return this.repository.createSession();
	}
	
	public void save(T session) {
		
		this.repository.save(session);
	}
	
	public T findById(String id) {
		
		return this.repository.findById(id);
	}
	
	public void deleteById(String id) {
		
		this.repository.deleteById(id);
	}
	
	public Map<String, T> findByPrincipalName(String principalName) {

		return this.repository.findByPrincipalName(principalName);
	}
	
	public Map<String, T> findByIndexNameAndIndexValue(String indexName, String indexValue) {
		
		return this.repository.findByIndexNameAndIndexValue(indexName, indexValue);
	}

	public FindByIndexNameSessionRepository<T> getRepository() {
		return repository;
	}

	public void setRepository(FindByIndexNameSessionRepository<T> sessions) {
		this.repository = sessions;
	}

}
