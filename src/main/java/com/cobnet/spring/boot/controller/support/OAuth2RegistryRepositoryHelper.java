package com.cobnet.spring.boot.controller.support;

import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.Base64File;
import com.cobnet.spring.boot.dto.OAuth2Registration;
import org.springframework.core.ResolvableType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OAuth2RegistryRepositoryHelper {

    public static List<OAuth2Registration> getRegistrationUrls() {

        String baseUrl = ProjectBeanHolder.getSecurityConfiguration().getOauth2().getAuthenticationUrl();

        List<OAuth2Registration> urls = new ArrayList<>();

        Iterable<ClientRegistration> registrations = null;

        ClientRegistrationRepository repository = ProjectBeanHolder.getClientRegistrationRepository();

        ResolvableType type = ResolvableType.forInstance(repository).as(Iterable.class);

        if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {

            registrations = (Iterable<ClientRegistration>) repository;
        }

        assert registrations != null;
        registrations.forEach(registration -> {

            String format = ".svg";

            try(InputStream stream = OAuth2RegistryRepositoryHelper.class.getResourceAsStream("/third-party/" + registration.getRegistrationId() + format)) {

                urls.add(new OAuth2Registration(registration.getClientName(), baseUrl + "/" + registration.getRegistrationId(), new Base64File(stream, format)));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return urls;
    }
}
