package com.storechain.security;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import com.storechain.common.AbstractSet;
import com.storechain.spring.boot.entity.ExternalUser;
import com.storechain.spring.boot.entity.User;
import com.storechain.utils.DatabaseManager;

public class OwnedExternalUserCollection extends AbstractSet<ExternalUser> {
	
	private final User owner;
	
	private final Set<ExternalUser> providers;
	
	public OwnedExternalUserCollection(User owner, Set<ExternalUser> providers) {
		
		this.owner = owner;
		this.providers = providers;
	}

	@Override
	protected Set<ExternalUser> getSet() {
		
		return this.providers;
	}
	
	@Override
    public boolean add(ExternalUser user) {
		
		if(DatabaseManager.getUserRepository() != null) {
			
			if(user.getUser() == null || !owner.equals(user.getUser())) {
				
				if(user.getUser() != null) {

					user.getUser().getOwnedExternalUserCollection().remove(user);

					if(DatabaseManager.getUserRepository() != null) {

						DatabaseManager.getUserRepository().save(user.getUser());
					}	
				}

				user.setUser(owner);
			}
		}

		if(DatabaseManager.getExternalUserRepository() != null) {
			
			Optional<ExternalUser> optional = DatabaseManager.getExternalUserRepository().findOne(Example.of(user, ExampleMatcher.matching().withIgnorePaths("created_time", "last_modified_time").withIgnoreCase()));
			
			if(optional.isEmpty()) {
				
				DatabaseManager.getExternalUserRepository().save(user);	
			} else {
				
				if(optional.get().getLastModfiedTime().before(user.getLastModfiedTime())) {
					
					DatabaseManager.getExternalUserRepository().save(user);
				}
			}
		}
		
		
		return super.add(user);
    }
	
	public Optional<ExternalUser> getByName(String name) {
		
		return this.stream().filter(user -> user.getIdentity().equalsIgnoreCase(name)).findFirst();
	}
}
