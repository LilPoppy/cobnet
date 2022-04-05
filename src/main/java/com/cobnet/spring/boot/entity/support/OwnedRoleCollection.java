package com.cobnet.spring.boot.entity.support;

import com.cobnet.common.wrapper.AbstractSetWrapper;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.ExternalUser;
import com.cobnet.spring.boot.entity.UserRole;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class OwnedRoleCollection extends AbstractSetWrapper<UserRole> {
	
	private final Set<UserRole> roles;
	
	public OwnedRoleCollection(Set<UserRole> roles) {
		
		this.roles = roles;
	}

	@Override
	protected Set<UserRole> getSet() {
		
		return this.roles;
	}
	
	@Override
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

}
