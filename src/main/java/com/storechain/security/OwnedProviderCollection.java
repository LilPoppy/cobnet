package com.storechain.security;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import com.storechain.common.AbstractSet;
import com.storechain.spring.boot.entity.UserProvider;
import com.storechain.utils.DatabaseManager;

public class OwnedProviderCollection extends AbstractSet<UserProvider> {
	
	private final Set<UserProvider> providers;
	
	public OwnedProviderCollection(Set<UserProvider> providers) {
		
		this.providers = providers;
	}

	@Override
	protected Set<UserProvider> getSet() {
		
		return this.providers;
	}
	
	@Override
    public boolean add(UserProvider provider) {
		
		if(DatabaseManager.getUserProviderRepository() != null) {
			
			Optional<UserProvider> optional = DatabaseManager.getUserProviderRepository().findOne(Example.of(provider, ExampleMatcher.matching().withIgnorePaths("created_time", "last_modified_time").withIgnoreCase()));
			
			if(optional.isEmpty()) {
				
				DatabaseManager.getUserProviderRepository().save(provider);	
			} else {
				
				if(optional.get().getLastModfiedTime().before(provider.getLastModfiedTime())) {
					
					DatabaseManager.getUserProviderRepository().save(provider);
				}
			}
		}
		
		return super.add(provider);
    }
	
	public Optional<UserProvider> getByName(String name) {
		
		return this.stream().filter(provider -> provider.getIdentity().equalsIgnoreCase(name)).findFirst();
	}
}
