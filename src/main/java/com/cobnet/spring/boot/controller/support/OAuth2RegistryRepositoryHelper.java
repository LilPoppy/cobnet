package com.cobnet.spring.boot.controller.support;

import com.cobnet.spring.boot.core.ProjectBeanHolder;
import org.springframework.core.ResolvableType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.util.HashMap;
import java.util.Map;

public class OAuth2RegistryRepositoryHelper {

    public static Map<String, String> getRegistrationUrls() {

        String baseUrl = ProjectBeanHolder.getSecurityConfiguration().getOauth2().getAuthenticationUrl();

        Map<String, String> urls = new HashMap<>();

        Iterable<ClientRegistration> registrations = null;

        ClientRegistrationRepository repository = ProjectBeanHolder.getClientRegistrationRepository();

        ResolvableType type = ResolvableType.forInstance(repository).as(Iterable.class);

        if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {

            registrations = (Iterable<ClientRegistration>) repository;
        }

        assert registrations != null;
        registrations.forEach(registration -> urls.put(registration.getClientName(), baseUrl + "/" + registration.getRegistrationId()));

        return urls;
    }
}
