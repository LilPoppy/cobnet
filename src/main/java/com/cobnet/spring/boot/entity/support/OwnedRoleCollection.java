package com.cobnet.spring.boot.entity.support;

import com.cobnet.common.wrapper.AbstractSetWrapper;
import com.cobnet.interfaces.cache.CacheKeyProvider;
import com.cobnet.interfaces.cache.annotation.SimpleCacheEvict;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.ExternalUser;
import com.cobnet.spring.boot.entity.User;
import com.cobnet.spring.boot.entity.UserRole;
import io.lettuce.core.support.caching.CacheAccessor;
import org.hibernate.cache.spi.entry.CacheEntry;
import org.springframework.cache.annotation.CacheEvict;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class OwnedRoleCollection extends AbstractSetWrapper<UserRole>  implements CacheKeyProvider<String> {

	private final User user;

	private final Set<UserRole> roles;
	
	public OwnedRoleCollection(User user, Set<UserRole> roles) {
		this.user = user;
		this.roles = roles;
	}

	@Override
	protected Set<UserRole> getSet() {
		
		return this.roles;
	}
	
	@Override
	@SimpleCacheEvict(cacheNames = "Users")
    public boolean add(UserRole role) {
		
		if(ProjectBeanHolder.getUserRoleRepository() != null) {

			Optional<UserRole> existed = ProjectBeanHolder.getUserRoleRepository().findByRoleEqualsIgnoreCase(role.getIdentity());

			if(existed.isEmpty()) {

				ProjectBeanHolder.getUserRoleRepository().save(role);

			} else if(existed.get().getLastModfiedTime().before(role.getLastModfiedTime())) {

				ProjectBeanHolder.getUserRoleRepository().saveAndFlush(role);
			}
		}
		
		return super.add(role);
    }

	@Override
	public boolean addAll(Collection<? extends UserRole> c) {

		boolean result = false;

		for(UserRole role : c) {

			result = this.add(role);
		}

		return result;
	}
	
	public Optional<UserRole> getByName(String name) {
		
		return this.stream().filter(role -> role.getName().equalsIgnoreCase(name)).findFirst();
	}

	@Override
	public String[] getKeys() {
		return new String[]{ user.getUsername() };
	}
}
