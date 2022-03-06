package com.storechain.security;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import com.storechain.common.AbstractSet;
import com.storechain.spring.boot.entity.UserProviderAuthority;
import com.storechain.utils.DatabaseManager;

public class OwnedProviderAuthorityCollection extends AbstractSet<UserProviderAuthority>  {
	
	private final Set<UserProviderAuthority> authorities;
	
	public OwnedProviderAuthorityCollection(Set<UserProviderAuthority> authorities) {
		
		this.authorities = authorities;
	}

	@Override
	protected Set<UserProviderAuthority> getSet() {
		
		return this.authorities;
	}
	
	@Override
    public boolean add(UserProviderAuthority authority) {
		
		if(DatabaseManager.getUserProviderAuthorityRepository() != null) {
			
			Optional<UserProviderAuthority> optional = DatabaseManager.getUserProviderAuthorityRepository().findOne(Example.of(authority, ExampleMatcher.matching().withIgnorePaths("created_time", "last_modified_time").withIgnoreCase()));
			
			if(optional.isEmpty()) {
				
				DatabaseManager.getUserProviderAuthorityRepository().save(authority);	
			} else {
				
				if(optional.get().getLastModfiedTime().before(authority.getLastModfiedTime())) {
					
					DatabaseManager.getUserProviderAuthorityRepository().save(authority);
				}
			}
		}
		
		return super.add(authority);
    }
	
	public Optional<UserProviderAuthority> getByName(String name) {
		
		return this.stream().filter(authority -> authority.getAuthority().equalsIgnoreCase(name)).findFirst();
	}
}
