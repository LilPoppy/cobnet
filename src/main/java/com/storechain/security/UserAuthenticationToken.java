package com.storechain.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import com.storechain.interfaces.security.Operator;

public class UserAuthenticationToken extends AbstractAuthenticationToken {

	private final Operator principal;
	
	private Object credentials;
	
	public UserAuthenticationToken(Operator principal, Object credentials) {
		
		super(principal.getAuthorities());
		
		this.principal = principal;
		this.credentials = credentials;
		this.setAuthenticated(true);
	}
	
	public UserAuthenticationToken(Operator principal) {
		
		this(principal, "");
	}
	

	@Override
	public Object getCredentials() {
		
		return this.credentials;
	}

	@Override
	public Operator getPrincipal() {
		
		return this.principal;
	}

}
