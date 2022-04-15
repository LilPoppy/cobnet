package com.cobnet.spring.boot.controller.restful;

import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.cobnet.spring.boot.dto.support.HumanValidationRequestStatus;
import com.cobnet.spring.boot.dto.support.HumanValidationValidateStatus;
import com.cobnet.spring.boot.service.support.AccountPhoneNumberVerifyCache;
import com.google.maps.errors.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpMethod;
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
    @PostMapping("/visitor/human-validate/validate")
    public HumanValidationValidate humanValidate(HttpServletRequest http, HttpServletResponse response, HumanValidationRequest request, int position) {

        HumanValidationValidate result = null;

        switch (request.type()) {

            case LOGIN -> result = ProjectBeanHolder.getHumanValidator().validate(http.getSession(true).getId(), position);
            case SMS_REQUEST -> {

                AccountPhoneNumberVerifyCache cache = ProjectBeanHolder.getPhoneNumberSmsVerifyService().getCache(request.username());

                if (cache == null) {

                    result = new HumanValidationValidate(HumanValidationValidateStatus.REJECTED);
                    break;
                }

                result = ProjectBeanHolder.getHumanValidator().validate(request.key(), position);
            }
        };

        response.setStatus(result.status().getCode());

        return result;
    }

    @Operation(summary = "Request new resources to verify is human operating.")
    @PostMapping("/visitor/human-validate/request")
    public HumanValidationRequestResult requestHumanValidation(HttpServletRequest http, HttpServletResponse response, HumanValidationRequest request) throws IOException {

        HumanValidationRequestResult result = null;

        switch (request.type()) {

            case LOGIN -> result = ProjectBeanHolder.getHumanValidator().create(http.getSession(true).getId());
            case SMS_REQUEST -> {

                AccountPhoneNumberVerifyCache cache = ProjectBeanHolder.getPhoneNumberSmsVerifyService().getCache(request.username());

                if (cache == null) {

                    result = new HumanValidationRequestResult(HumanValidationRequestStatus.REJECTED);
                    break;
                }

                result = ProjectBeanHolder.getHumanValidator().create(request.key());
            }
        }

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

        PhoneNumberSmsVerifyResult result = ProjectBeanHolder.getPhoneNumberSmsVerifyService().request(verify);

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
