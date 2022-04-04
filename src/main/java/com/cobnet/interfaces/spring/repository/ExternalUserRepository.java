package com.cobnet.interfaces.spring.repository;

import com.cobnet.security.permission.UserPermission;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.ExternalUser;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Cacheable("ExternalUsers")
public interface ExternalUserRepository extends JPABaseRepository<ExternalUser, String>, OAuth2UserService<OidcUserRequest, OidcUser> {

    @Override
    public default ExternalUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {

        OidcUser oidc = ProjectBeanHolder.getOidcUserService().loadUser(request);

        String username = "";

        String provider = request.getClientRegistration().getRegistrationId();

        String email = oidc.getEmail();

        Set<UserPermission> authorities = oidc.getAuthorities().stream().map(authority -> new UserPermission(authority.getAuthority())).collect(Collectors.toSet());

        HashMap<String, Object> attributes = new HashMap<>(oidc.getAttributes());

        if(email != null && email.length() > 0 && oidc.getEmailVerified()) {

            username = email;
        }

        String phone = oidc.getPhoneNumber();

        if(phone != null && phone.length() > 0 && oidc.getPhoneNumberVerified()) {

            username = phone;
        }

        Optional<ExternalUser> optional =  this.findByProviderAndUsernameIgnoreCase(provider, username);

        if(optional.isPresent()) {

            ExternalUser existed = optional.get();
            existed.setIdToken(request.getIdToken().getTokenValue());
            existed.setAccessToken(request.getAccessToken().getTokenValue());

            Map<String, Object> map = existed.getAttributes();

            for(String key : attributes.keySet()) {

                Object existedValue = map.get(key);

                Object value = attributes.get(key);

                if(existedValue == null) {

                    map.put(key, value);
                    continue;
                }

                if(!existedValue.equals(value)) {

                    map.put(key, value);
                }
            }

            existed.getOwnedPermissionCollection().addAll(authorities);

            return existed;
        }

        ExternalUser existed = new ExternalUser(username, provider, request.getIdToken().getTokenValue(), request.getAccessToken().getTokenValue(), authorities, attributes);

        this.save(existed);

        return existed;
    }

    public Optional<ExternalUser> findByIdTokenEquals(@Param("idToken") String idToken);

    public Optional<ExternalUser> findByIdentityEqualsIgnoreCase(@Param("identity") String identity);

    public default Optional<ExternalUser> findByProviderAndUsernameIgnoreCase(String provider, String username) {

        return this.findByIdentityEqualsIgnoreCase(provider + ":" + username);
    }
}
