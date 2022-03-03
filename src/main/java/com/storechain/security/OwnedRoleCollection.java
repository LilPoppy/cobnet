package com.storechain.security;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import com.storechain.common.AbstractSet;
import com.storechain.interfaces.security.permission.Permissible;
import com.storechain.spring.boot.entity.EntityBase;
import com.storechain.spring.boot.entity.UserRole;
import com.storechain.utils.DatabaseManager;

public class OwnedRoleCollection extends AbstractSet<UserRole> {
	
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
		
		if(DatabaseManager.getUserRoleRepository() != null) {
			
			Optional<UserRole> optional = DatabaseManager.getUserRoleRepository().findOne(Example.of(role, ExampleMatcher.matching().withIgnorePaths("created_time", "last_modified_time").withIgnoreCase()));
			
			if(optional.isEmpty()) {
				
				DatabaseManager.getUserRoleRepository().save(role);	
			} else {
				
				if(optional.get().getLastModfiedTime().before(role.getLastModfiedTime())) {
					
					DatabaseManager.getUserRoleRepository().save(role);
				}
			}
		}
		
		return super.add(role);
    }
	
	public Optional<UserRole> getByName(String name) {
		
		return this.stream().filter(role -> role.getRole().equalsIgnoreCase(name)).findFirst();
	}

}
