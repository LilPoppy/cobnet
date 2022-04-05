package com.cobnet.spring.boot.entity.support;

import com.cobnet.common.wrapper.AbstractSetWrapper;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.ExternalUser;
import com.cobnet.spring.boot.entity.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class OwnedExternalUserCollection extends AbstractSetWrapper<ExternalUser> {
	
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
		
		if(ProjectBeanHolder.getUserRepository() != null) {
			
			if(user.getUser() == null || !owner.equals(user.getUser())) {
				
				if(user.getUser() != null) {

					user.getUser().getOwnedExternalUserCollection().remove(user);

					if(ProjectBeanHolder.getUserRepository() != null) {

						ProjectBeanHolder.getUserRepository().save(user.getUser());
					}	
				}

				user.setUser(owner);
			}
		}

		if(ProjectBeanHolder.getExternalUserRepository() != null) {

			Optional<ExternalUser> existed = ProjectBeanHolder.getExternalUserRepository().findByIdentityEqualsIgnoreCase(user.getIdentity());

			if(existed.isEmpty()) {
				
				ProjectBeanHolder.getExternalUserRepository().save(user);

			} else if(existed.get().getLastModfiedTime().before(user.getLastModfiedTime())) {
					
					ProjectBeanHolder.getExternalUserRepository().save(user);
			}
		}

		return super.add(user);
    }

	@Override
	public boolean addAll(Collection<? extends ExternalUser> c) {

		boolean result = false;

		for(ExternalUser user : c) {

			result = this.add(user);
		}

		return result;
	}
	
	public Optional<ExternalUser> getByName(String name) {
		
		return this.stream().filter(user -> user.getIdentity().equalsIgnoreCase(name)).findFirst();
	}
}
