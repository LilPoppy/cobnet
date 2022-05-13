package com.cobnet.spring.boot.controller.restful;

import com.cobnet.spring.boot.controller.support.OAuth2RegistryRepositoryHelper;
import com.cobnet.spring.boot.dto.OAuth2Registration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "OAuth2")
@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {

    @Operation(summary = "Get OAuth2 registrations.", description = "List of supported oauth2 registration with name and url.")
    @GetMapping("/registration-urls")
    public List<OAuth2Registration> oauth2RegistrationUrls() {

        return OAuth2RegistryRepositoryHelper.getRegistrationUrls();
    }
}
