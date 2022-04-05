package com.cobnet.spring.boot.entity.support;

import com.cobnet.interfaces.spring.repository.ExternalUserRepository;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component("StringOidcUserRequestCacheKeyGenerator")
public class StringOidcUserRequestCacheKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {

        if(target instanceof ExternalUserRepository) {

            for(Object param : params) {

                if(param instanceof OidcUserRequest request) {

                    String provider = request.getClientRegistration().getRegistrationId();

                    OidcIdToken token = request.getIdToken();

                    if(token.getPhoneNumberVerified() != null && token.getPhoneNumberVerified()) {

                        return provider + ":" + token.getPhoneNumber();
                    }

                    return provider + ":" + token.getEmail();
                }
            }
        }

        return params;
    }
}
