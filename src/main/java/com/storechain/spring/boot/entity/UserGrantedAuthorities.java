package com.storechain.spring.boot.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.springframework.security.core.GrantedAuthority;

@Entity
public class UserGrantedAuthorities {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@OneToOne(targetEntity = User.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "user", referencedColumnName = "username")
	private User user;
	
	@ManyToOne(targetEntity = UserGrantedAuthority.class, cascade =  CascadeType.ALL)
	@JoinColumn(name = "authorities", referencedColumnName = "authority")
	@ElementCollection
	private Collection<UserGrantedAuthority> authorities;
	
	public UserGrantedAuthorities() {}
	
	public UserGrantedAuthorities(User user, Collection<? extends GrantedAuthority> authorities) {
		
		this.user = user;
		this.authorities = authorities.stream().map(role -> new UserGrantedAuthority(role.getAuthority())).toList();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Collection<UserGrantedAuthority> authorities) {
		
		this.authorities = new ArrayList<UserGrantedAuthority>(authorities);
	}
	
	public <T extends GrantedAuthority> void addAuthority(T... authorities) {
		
		this.authorities = Arrays.stream(authorities).map(role -> new UserGrantedAuthority(role.getAuthority())).toList();
	}
	
}
