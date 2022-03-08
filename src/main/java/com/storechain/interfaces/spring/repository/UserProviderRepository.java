package com.storechain.interfaces.spring.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

import com.storechain.spring.boot.entity.UserProvider;

public interface UserProviderRepository extends ExtensionRepository<UserProvider, String>, OAuth2UserService<OidcUserRequest, UserProvider> {

	public default UserProvider loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {
		
		return this.findByIdToken(request.getIdToken().getTokenValue());
	}
	
	public UserProvider findByIdToken(@Param("idToken") String idToken);
	
	public UserProvider findByIdentityIgnoreCase(@Param("identity") String identity);
	
	public default UserProvider findByIdentityIgnoreCase(String identity, String provider) {
		
		return this.findByIdentityIgnoreCase(identity + ":" + provider);
	}
}
