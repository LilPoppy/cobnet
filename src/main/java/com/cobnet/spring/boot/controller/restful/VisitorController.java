package com.cobnet.spring.boot.controller.restful;

import com.cobnet.interfaces.security.Account;
import com.cobnet.security.AccountAuthenticationToken;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.cobnet.spring.boot.dto.support.*;
import com.cobnet.spring.boot.entity.User;
import com.google.maps.errors.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.NotSupportedException;
import java.io.IOException;

@Tag(name = "Visitor")
@RestController
public class VisitorController {

    @Operation(summary = "Check this session is has been authenticated.")
    @GetMapping("/visitor/is-authenticated")
    public boolean isAuthenticated() {

        SecurityContext context = SecurityContextHolder.getContext();

        return context != null && ((context.getAuthentication() instanceof AccountAuthenticationToken account && account.isAuthenticated()) || (context.getAuthentication() instanceof OAuth2AuthenticationToken oauth2 && oauth2.isAuthenticated()));
    }

    @Operation(summary = "Create session for visitor.")
    @RequestMapping(value = "/visitor/check-in", method = RequestMethod.OPTIONS)
    public boolean checkIn(HttpServletRequest request) {

        request.getSession(true);

        return true;
    }

    @Operation(summary = "Login in default way.", description = "")
    @PostMapping("/visitor/login")
    public ResponseResult<AuthenticationStatus> login(String username, String password, @RequestParam(name = "remember-me") boolean rememberMe) throws NotSupportedException {

        throw new NotSupportedException();
    }

    @Operation(summary = "Validate is human operation.")
    @PostMapping("/visitor/human-validate")
    public ResponseResult<HumanValidationValidateStatus> humanValidate(HttpServletRequest http, HttpServletResponse response, int position) {

        ResponseResult<HumanValidationValidateStatus> result = ProjectBeanHolder.getHumanValidator().validate(http.getSession(true).getId(), position);

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
    public ResponseResult<HumanValidationRequestStatus> humanValidate(HttpServletRequest http, HttpServletResponse response) throws IOException {

        ResponseResult<HumanValidationRequestStatus> result = ProjectBeanHolder.getHumanValidator().create(http.getSession(true).getId());

        response.setStatus(result.status().getCode());

        return result;
    }

    @Operation(summary = "Request sms verify for provided phone number.")
    @PostMapping("/visitor/sms/request")
    public ResponseResult<PhoneNumberSmsRequestResultStatus> phoneNumberSmsRequest(HttpServletResponse response, PhoneNumberSmsRequest request) throws IOException {

        ResponseResult<PhoneNumberSmsRequestResultStatus> result = ProjectBeanHolder.getPhoneNumberSmsVerifyService().request(request);

        response.setStatus(result.status().getCode());

        return result;
    }

    @Operation(summary = "Verify sms code from record.")
    @PostMapping("/visitor/sms/verify")
    public ResponseResult<PhoneNumberSmsVerifyResultStatus> phoneNumberSmsVerify(HttpServletResponse response, PhoneNumberSmsVerify verify) {

        ResponseResult<PhoneNumberSmsVerifyResultStatus> result = ProjectBeanHolder.getPhoneNumberSmsVerifyService().verify(verify);

        response.setStatus(result.status().getCode());

        return result;

    }

    @Operation(summary = "Register an new account.", description = "")
    @PostMapping("/visitor/register")
    public ResponseResult<UserRegisterResultStatus> register(HttpServletResponse response, UserRegisterForm form, AddressForm address) {

        ResponseResult<UserRegisterResultStatus> result = ProjectBeanHolder.getAccountService().register(form, address);

        response.setStatus(result.status().getCode());

        return result;
    }

    @Operation(summary = "auto complete given address.")
    @PostMapping("/visitor/autocomplete/address")
    public ResponseResult<GoogleApiRequestResultStatus> autocompleteAddress(HttpServletRequest request, HttpServletResponse response, AddressForm addressRequest) throws IOException, InterruptedException, ApiException {

        ResponseResult<GoogleApiRequestResultStatus> result =  ProjectBeanHolder.getGoogleMapService().autocompleteRequest(request, null, addressRequest);

        response.setStatus(result.status().getCode());

        return result;
    }

}
