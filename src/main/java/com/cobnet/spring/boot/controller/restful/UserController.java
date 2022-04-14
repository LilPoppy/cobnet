package com.cobnet.spring.boot.controller.restful;

import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.cobnet.spring.boot.dto.support.HumanValidationRequestStatus;
import com.cobnet.spring.boot.dto.support.HumanValidationRequestType;
import com.cobnet.spring.boot.dto.support.HumanValidationValidateStatus;
import com.cobnet.spring.boot.service.support.AccountPhoneNumberVerifyCache;
import com.google.maps.PlaceAutocompleteRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AutocompletePrediction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Tag(name = "User")
@RestController
public class UserController {

    @Operation(summary = "Login in default way.", description = "")
    @PostMapping("/user/login")
    public AuthenticationResult login(String username, String password, @RequestParam(name = "remember-me") boolean rememberme) {

        throw new RuntimeException("apidoc");
    }

    @PostMapping("/user/test")
    public AutocompletePrediction[] autoCompleteRequest(HttpServletRequest request, String text) throws IOException, InterruptedException, ApiException {


        return ProjectBeanHolder.getGoogleMap().placeAutocompleteRequest(text, request.getSession(true)).await();
    }

    @Operation(summary = "Logout user")
    @PostMapping("/user/logout")
    public void logout() {
        throw new RuntimeException("apidoc");
    }

    @Operation(summary = "Validate is human operation.")
    @PostMapping("/user/human-validate/validate")
    public HumanValidationValidate humanValidate(HttpServletRequest http, HttpServletResponse response, HumanValidationRequest request, int position) {

        HumanValidationValidate result = null;

        switch (request.type()) {
            case LOGIN -> result = ProjectBeanHolder.getHumanValidator().validate(http.getSession(true).getId(), position);
            case SMS_REQUEST -> {

                AccountPhoneNumberVerifyCache cache = ProjectBeanHolder.getAccountService().getPhoneNumberVerifyCache(request.username());

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
    @PostMapping("/user/human-validate/request")
    public HumanValidationRequestResult requestHumanValidation(HttpServletRequest http, HttpServletResponse response, HumanValidationRequest request) throws IOException {

        HumanValidationRequestResult result = null;

        switch (request.type()) {
            case LOGIN -> result = ProjectBeanHolder.getHumanValidator().create(http.getSession(true).getId());
            case SMS_REQUEST -> {

                AccountPhoneNumberVerifyCache cache = ProjectBeanHolder.getAccountService().getPhoneNumberVerifyCache(request.username());

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
    @PostMapping("/user/sms/request")
    public PhoneNumberSmsRequestResult phoneNumberSmsRequest(HttpServletResponse response, PhoneNumberSmsRequest request) throws IOException {

        PhoneNumberSmsRequestResult result = ProjectBeanHolder.getAccountService().requestPhoneNumberSms(request);

        response.setStatus(result.status().getCode());

        return result;
    }

    @Operation(summary = "Verify sms code from record.")
    @PostMapping("/user/sms/verify")
    public PhoneNumberSmsVerifyResult phoneNumberSmsVerify(HttpServletResponse response, PhoneNumberSmsVerify verify) {

        PhoneNumberSmsVerifyResult result = ProjectBeanHolder.getAccountService().requestPhoneNumberVerify(verify);

        response.setStatus(result.status().getCode());

        return result;

    }

    @Operation(summary = "Register an new account.", description = "")
    @PostMapping("/user/register")
    public UserRegisterResult register(HttpServletResponse response, UserRegisterForm form) {

        UserRegisterResult result = ProjectBeanHolder.getAccountService().register(form);

        response.setStatus(result.status().getCode());

        return result;
    }


}
