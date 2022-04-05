package com.cobnet.interfaces.spring.repository;

import com.cobnet.security.permission.UserPermission;
import com.cobnet.spring.boot.entity.ExternalUser;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@Cacheable(value = "ExternalUsers", unless="#result == null", keyGenerator = "StringOidcUserRequestCacheKeyGenerator")
public interface ExternalUserRepository extends JPABaseRepository<ExternalUser, String>, OAuth2UserService<OidcUserRequest, OidcUser> {

    @Override
    public default ExternalUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {

        String username = "";

        OidcIdToken token = request.getIdToken();

        String provider = request.getClientRegistration().getRegistrationId();

        Set<UserPermission> authorities = request.getAccessToken().getScopes().stream().map(scope -> new UserPermission("SCOPE_" + scope)).collect(Collectors.toSet());

        if(token.getEmailVerified() != null && token.getEmailVerified()) {

            username = token.getEmail();
        }

        if(token.getPhoneNumberVerified() != null && token.getPhoneNumberVerified()) {

            username = token.getPhoneNumber();
        }

        Map<String, Object> attributes = Stream.of(token.getClaims().entrySet()).flatMap(Collection::stream).collect(Collectors.toMap(Map.Entry<String, Object>::getKey, Map.Entry<String, Object>::getValue));

        ExternalUser user = new ExternalUser(username, provider, request.getIdToken().getTokenValue(), request.getAccessToken().getTokenValue(), authorities, attributes);

        Optional<ExternalUser> optional = this.findByProviderAndUsernameIgnoreCase(provider, username);

        if(optional.isPresent()) {

            if(!optional.get().equals(user)) {

                this.save(user);
            }

        } else {

            this.save(user);
        }

        return user;
    }

    public Optional<ExternalUser> findByIdTokenEquals(@Param("idToken") String idToken);

    public Optional<ExternalUser> findByIdentityEqualsIgnoreCase(@Param("identity") String identity);

    public default Optional<ExternalUser> findByProviderAndUsernameIgnoreCase(String provider, String username) {

        return this.findByIdentityEqualsIgnoreCase(provider + ":" + username);
    }
}
