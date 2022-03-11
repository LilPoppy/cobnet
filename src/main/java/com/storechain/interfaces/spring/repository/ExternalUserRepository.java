package com.storechain.interfaces.spring.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

import com.storechain.spring.boot.entity.ExternalUser;

public interface ExternalUserRepository extends ExtensionRepository<ExternalUser, String>, OAuth2UserService<OidcUserRequest, ExternalUser> {

	public default ExternalUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {
		
		return this.findByIdToken(request.getIdToken().getTokenValue());
	}
	
	public ExternalUser findByIdToken(@Param("idToken") String idToken);
	
	public ExternalUser findByIdentityIgnoreCase(@Param("identity") String identity);
	
	public default ExternalUser findByProvidereAndUsernameIgnoreCase(String provider, String username) {
		
		return this.findByIdentityIgnoreCase(provider + ":" + username);
	}
	
}
