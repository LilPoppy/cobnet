package com.storechain.spring.boot.service;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import com.storechain.interfaces.spring.repository.ExtensionRepository;


public class JpaExtensionRepository<T, ID extends Serializable>  extends SimpleJpaRepository<T,ID> implements ExtensionRepository<T,ID> {

	private final EntityManager entityManager;
	
	public JpaExtensionRepository(Class<T> domainClass, EntityManager em) {
		super(domainClass, em);
		this.entityManager = em;
	}
	
	public JpaExtensionRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
		super(entityInformation, em);
		this.entityManager = em;
	}

}
