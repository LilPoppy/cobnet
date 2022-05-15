package com.cobnet.spring.boot.service;

import com.cobnet.interfaces.spring.repository.ExternalUserRepository;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Arrays;

@Service
public class RedisCacheKeyGenerator implements KeyGenerator {

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

        return String.join(":", Arrays.stream(params).filter(param -> param != null).map(Object::toString).toList());
    }
}
