package com.storechain.utils;


import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Consumer;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

import com.storechain.common.CommonConverter;
import com.storechain.interfaces.spring.repository.UserPermissionRepository;
import com.storechain.interfaces.spring.repository.UserRepository;
import com.storechain.interfaces.spring.repository.UserRoleRepository;
import com.storechain.spring.boot.service.JsonRedisService;
import com.storechain.spring.boot.service.SimpleSessionRepository;

@Component
public class DatabaseManager {

	private static UserRepository USER_REPOSITORY;
	
	private static UserRoleRepository USER_ROLE_REPOSITORY;
	
	private static UserPermissionRepository USER_PERMISSION_REPOSITORY;
	
	public DatabaseManager() {}

	public static <T extends JsonRedisService<K, V> ,K, V> T getJsonRedisService(Class<K> key, Class<V> value) {
		
		return  (T) SpringContext.getContext().getBeanProvider(ResolvableType.forClassWithGenerics(JsonRedisService.class, key, value)).getObject();
	}
	
	public static <E extends SimpleSessionRepository<T>, T extends Session> T getSimpleSessionRepository(Class<T> session) {
		
		return (T) SpringContext.getContext().getBeanProvider(ResolvableType.forClassWithGenerics(SimpleSessionRepository.class, session)).getObject();
	}
	
	
	public static <E extends JpaRepository<T, ID>, T, ID extends Serializable, RESULT> RESULT commitJpaRepository(Class<? extends E> repo, Function<E, RESULT> predicate) {
		
		var context = SpringContext.getContext();
		
		if(context == null) {
			
			throw new ApplicationContextException("Spring Application is not initialized.");
		}
		
		EntityManager manager = context.getBean(EntityManager.class);
		
		JpaRepositoryFactory factory = new JpaRepositoryFactory(manager);
		
		try {
			
			manager.getTransaction().begin();
			
			return predicate.apply(factory.getRepository(repo));
			
		} finally {
			
			manager.getTransaction().commit();
		}

	}
	
	public static <E extends JpaRepository<T, ID>, T, ID extends Serializable, RESULT> void commitJpaRepository(Class<? extends E> repo, Consumer<E> action) {
		
		commitJpaRepository(repo, CommonConverter.adapt(action));
	}
	
	public static UserRepository getUserRepository() {
		
		return DatabaseManager.USER_REPOSITORY;
	}
	
	public static UserRoleRepository getUserRoleRepository() {
		
		return DatabaseManager.USER_ROLE_REPOSITORY;
	}
	
	public static UserPermissionRepository getUserPermissionRepository() {
		
		return DatabaseManager.USER_PERMISSION_REPOSITORY;
	}
	
	@Component
	final static class DatabaseManagerBean {
		
		@Autowired
		public void setUserRepository(UserRepository repository) {
			
			DatabaseManager.USER_REPOSITORY = repository;
		}
		
		@Autowired
		public void setUserRoleRepository(UserRoleRepository repository) {
			
			DatabaseManager.USER_ROLE_REPOSITORY = repository;
		}
		
		@Autowired
		public void setUserPermissionRepository(UserPermissionRepository repository) {
			
			DatabaseManager.USER_PERMISSION_REPOSITORY = repository;
		}
	}

}
