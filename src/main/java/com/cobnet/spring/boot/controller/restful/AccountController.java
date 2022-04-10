package com.cobnet.spring.boot.controller.restful;

import com.cobnet.spring.boot.controller.support.OAuth2RegistryRepositoryHelper;
import com.cobnet.spring.boot.dto.OAuth2Registration;
import com.cobnet.spring.boot.dto.RegisterForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "Account")
@RestController
public class AccountController {

    @Operation(summary = "Get OAuth2 registrations.", description = "List of supported oauth2 registration with name and url.")
    @GetMapping("/oauth2/registration-urls")
    public List<OAuth2Registration> oauth2RegistrationUrls() {

        return OAuth2RegistryRepositoryHelper.getRegistrationUrls();
    }

    @Operation(summary = "Request an account register.", description = "")
    @PostMapping("/register")
    public String register(RegisterForm form) {

        //TODO Verification code
        System.out.println(form);

        return "OK";
    }
}
