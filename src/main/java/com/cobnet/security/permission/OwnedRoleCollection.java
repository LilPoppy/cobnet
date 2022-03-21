package com.cobnet.security.permission;

import com.cobnet.common.collection.AbstractSetWrapper;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.UserRole;

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
	
	public Optional<UserRole> getByName(String name) {
		
		return this.stream().filter(role -> role.getName().equalsIgnoreCase(name)).findFirst();
	}

}
