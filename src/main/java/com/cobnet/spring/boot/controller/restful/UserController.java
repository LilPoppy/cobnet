package com.cobnet.spring.boot.controller.restful;

import com.cobnet.interfaces.security.annotation.AccessSecured;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
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

    @Operation(summary = "Logout user")
    @PostMapping("/user/logout")
    public void logout() {
        throw new RuntimeException("apidoc");
    }

    @Operation(summary = "Validate is human operation.")
    @PostMapping("/user/human-validate/validate")
    public HumanValidationValidate humanValidate(HttpServletRequest request, HttpServletResponse response, int position) {

        HumanValidationValidate result = ProjectBeanHolder.getHumanValidator().imageValidate(request.getSession(true).getId(), position);

        response.setStatus(result.status().getCode());

        return result;
    }

    @Operation(summary = "Request new resources to verify is human operating.")
    @GetMapping("/user/human-validate/request")
    public HumanValidationRequest requestHumanValidation(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HumanValidationRequest result = ProjectBeanHolder.getHumanValidator().createImageValidation(request.getSession(true).getId());

        response.setStatus(result.status().getCode());

        return result;
    }

    @Operation(summary = "Request sms verify for provided phone number.")
    @PostMapping("/user/sms/request")
    public PhoneNumberSmsRequestResult phoneNumberSmsRequest(HttpServletResponse response, PhoneNumberSmsRequest request) {

        PhoneNumberSmsRequestResult result = ProjectBeanHolder.getAccountService().requestPhoneNumberSms(request);

        response.setStatus(result.status().getCode());

        return result;
    }

    @Operation(summary = "Verify sms code on record.")
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
