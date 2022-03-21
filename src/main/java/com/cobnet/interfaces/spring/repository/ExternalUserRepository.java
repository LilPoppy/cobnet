package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.ExternalUser;
import org.springframework.data.repository.query.Param;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExternalUserRepository extends JPABaseRepository<ExternalUser, String>, OAuth2UserService<OidcUserRequest, ExternalUser> {

    @Override
    public default ExternalUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {

        return this.findByIdTokenEquals(request.getIdToken().getTokenValue()).orElse(null);
    }

    public Optional<ExternalUser> findByIdTokenEquals(@Param("idToken") String idToken);

    public Optional<ExternalUser> findByIdentityEqualsIgnoreCase(@Param("identity") String identity);

    public default Optional<ExternalUser> findByProviderAndUsernameIgnoreCase(String provider, String username) {

        return this.findByIdentityEqualsIgnoreCase(provider + ":" + username);
    }

}
