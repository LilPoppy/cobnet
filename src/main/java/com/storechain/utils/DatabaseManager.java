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

import com.storechain.spring.boot.service.JsonRedisService;
import com.storechain.spring.boot.service.SimpleSessionRepository;

@Component
public class DatabaseManager {
	
	private static ApplicationContext CONTEXT;

	@Autowired
	public void setContext(ApplicationContext context) {
		
		this.CONTEXT = context;
	}
	
	public static <T extends JsonRedisService<K, V> ,K, V> T getJsonRedisService(Class<K> key, Class<V> value) {
		
		return  (T) CONTEXT.getBeanProvider(ResolvableType.forClassWithGenerics(JsonRedisService.class, key, value)).getObject();
	}
	
	public static <E extends SimpleSessionRepository<T>, T extends Session> T getSimpleSessionRepository(Class<T> session) {
		
		return (T) CONTEXT.getBeanProvider(ResolvableType.forClassWithGenerics(SimpleSessionRepository.class, session)).getObject();
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

}
