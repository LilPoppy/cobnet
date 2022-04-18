package com.cobnet.spring.boot.controller.restful;

import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.google.maps.errors.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Tag(name = "Visitor")
@RestController
public class VisitorController {

    @Operation(summary = "Create session for visitor.")
    @RequestMapping(value = "/visitor/check-in", method = RequestMethod.OPTIONS)
    public boolean checkIn(HttpServletRequest request) {

        request.getSession(true);

        return true;
    }

    @Operation(summary = "Login in default way.", description = "")
    @PostMapping("/visitor/login")
    public AuthenticationResult login(String username, String password, @RequestParam(name = "remember-me") boolean rememberme) {

        throw new RuntimeException("apidoc");
    }

    @Operation(summary = "Validate is human operation.")
    @PostMapping("/visitor/human-validate")
    public HumanValidationValidate humanValidate(HttpServletRequest http, HttpServletResponse response, int position) {

        HumanValidationValidate result = ProjectBeanHolder.getHumanValidator().validate(http.getSession(true).getId(), position);

        response.setStatus(result.status().getCode());

        return result;
    }

    @Operation(summary = "Check is cache human validated.")
    @GetMapping( "/visitor/human-validate")
    public boolean humanValidate(HttpServletRequest http) {

        return ProjectBeanHolder.getHumanValidator().isValidated(http.getSession(true).getId());
    }

    @Operation(summary = "Request new resources to verify is human operating.")
    @RequestMapping(method = RequestMethod.PATCH,value = "/visitor/human-validate")
    public HumanValidationRequestResult humanValidate(HttpServletRequest http, HttpServletResponse response) throws IOException {

        HumanValidationRequestResult result = ProjectBeanHolder.getHumanValidator().create(http.getSession(true).getId());

        response.setStatus(result.status().getCode());

        return result;
    }

    @Operation(summary = "Request sms verify for provided phone number.")
    @PostMapping("/visitor/sms/request")
    public PhoneNumberSmsRequestResult phoneNumberSmsRequest(HttpServletResponse response, PhoneNumberSmsRequest request) throws IOException {

        PhoneNumberSmsRequestResult result = ProjectBeanHolder.getPhoneNumberSmsVerifyService().request(request);

        response.setStatus(result.status().getCode());

        return result;
    }

    @Operation(summary = "Verify sms code from record.")
    @PostMapping("/visitor/sms/verify")
    public PhoneNumberSmsVerifyResult phoneNumberSmsVerify(HttpServletResponse response, PhoneNumberSmsVerify verify) {

        PhoneNumberSmsVerifyResult result = ProjectBeanHolder.getPhoneNumberSmsVerifyService().verify(verify);

        response.setStatus(result.status().getCode());

        return result;

    }

    @Operation(summary = "Register an new account.", description = "")
    @PostMapping("/visitor/register")
    public UserRegisterResult register(HttpServletResponse response, UserRegisterForm form, AddressForm address) {

        UserRegisterResult result = ProjectBeanHolder.getAccountService().register(form, address);

        response.setStatus(result.status().getCode());

        return result;
    }

    @Operation(summary = "auto complete given address.")
    @PostMapping("/visitor/autocomplete/address")
    public AutocompleteResult autocompleteAddress(HttpServletRequest request, HttpServletResponse response, AddressForm addressRequest) throws IOException, InterruptedException, ApiException {

        AutocompleteResult result =  ProjectBeanHolder.getGoogleMapService().autocompleteRequest(request, addressRequest);

        response.setStatus(result.status().getCode());

        return result;
    }

}
