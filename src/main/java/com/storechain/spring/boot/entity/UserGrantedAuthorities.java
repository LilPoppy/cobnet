package com.storechain.spring.boot.entity;

import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import lombok.Data;

@Entity
@Data
@Table(name = "user-authorities")
public class UserGrantedAuthorities {

	@Id
	@OneToOne(targetEntity = User.class, mappedBy = "username")
	private User user;
	
	@OneToMany(targetEntity = UserGrantedAuthority.class, mappedBy = "authority")
	private List<UserGrantedAuthority> authorities;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<UserGrantedAuthority> authorities) {
		this.authorities = authorities;
	}
	
	public static UserGrantedAuthorities fromData(User user, List<UserGrantedAuthority> authorities) {
		
		return new UserGrantedAuthorities() {{
			
			this.setUser(user);
			this.setAuthorities(authorities);
		}};
	}
	
	public static UserGrantedAuthorities fromDataA(User user, List<GrantedAuthority> authorities) {
		return new UserGrantedAuthorities() {{
			
			this.setUser(user);
			this.setAuthorities(authorities.stream().map(authority -> UserGrantedAuthority.fromData(authority.getAuthority())).toList());
		}};
	}
}
