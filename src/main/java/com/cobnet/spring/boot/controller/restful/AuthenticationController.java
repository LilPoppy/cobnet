package com.cobnet.spring.boot.controller.restful;

import com.cobnet.spring.boot.controller.support.OAuth2RegistryRepositoryHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthenticationController {

    @GetMapping("/oauth2/registration-urls")
    public Map<String,String> oauth2RegistrationUrls() {

        return OAuth2RegistryRepositoryHelper.getRegistrationUrls();
    }
}
