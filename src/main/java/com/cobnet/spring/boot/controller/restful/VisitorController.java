package com.cobnet.spring.boot.controller.restful;

import com.cobnet.common.PuzzledImage;
import com.cobnet.exception.*;
import com.cobnet.exception.support.*;
import com.cobnet.interfaces.security.annotation.HumanValidationRequired;
import com.cobnet.security.AccountAuthenticationToken;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.cobnet.spring.boot.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.FlushMode;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.NotSupportedException;
import java.io.IOException;

@Tag(name = "Visitor")
@RestController
@RequestMapping("/visitor")
public class VisitorController {

    @Operation(summary = "Check this session is has been authenticated.")
    @GetMapping("/is-authenticated")
    public boolean isAuthenticated() {

        SecurityContext context = SecurityContextHolder.getContext();

        return context != null && ((context.getAuthentication() instanceof AccountAuthenticationToken account && account.isAuthenticated()) || (context.getAuthentication() instanceof OAuth2AuthenticationToken oauth2 && oauth2.isAuthenticated()));
    }

    @Operation(summary = "Create session for visitor.")
    @RequestMapping(value = "/check-in", method = RequestMethod.OPTIONS)
    public boolean checkIn(HttpServletRequest request) {

        request.getSession(true);

        return true;
    }

    @Operation(summary = "Login in default way.", description = "")
    @PostMapping("/login")
    public ResponseResult<AuthenticationStatus> login(String username, String password, @RequestParam(name = "remember-me") boolean rememberMe) throws NotSupportedException {

        throw new NotSupportedException();
    }

    @Operation(summary = "Validate is human operation.")
    @PostMapping("/human-validate")
    public ResponseResult<HumanValidationValidateStatus> humanValidate(HttpServletRequest http, int position) throws ResponseFailureStatusException {

        if(ProjectBeanHolder.getHumanValidator().validate(http.getSession(true).getId(), position)) {

            return new ResponseResult<>(HumanValidationValidateStatus.SUCCESS);
        }

        return null;
    }

    @Operation(summary = "Check is cache human validated.")
    @GetMapping( "/human-validate")
    public boolean humanValidate(HttpServletRequest http) {

        return ProjectBeanHolder.getHumanValidator().isValidated(http.getSession(true).getId());
    }

    @Operation(summary = "Request new resources to verify is human operating.")
    @RequestMapping(method = RequestMethod.PATCH,value = "/human-validate")
    public ResponseResult<HumanValidationRequestStatus> humanValidate(HttpServletRequest http, HttpServletResponse response) throws IOException, ResponseFailureStatusException {

        PuzzledImage image = ProjectBeanHolder.getHumanValidator().create(http.getSession(true).getId());

        if(image != null) {

            return new ResponseResult<>(HumanValidationRequestStatus.SUCCESS, new ObjectWrapper<>("y-axis", image.getJigsawY()), new Base64Image(image.getImage(), "png"), new Base64Image(image.getJigsawImage(), "png"));
        }

        return null;
    }

    @HumanValidationRequired
    @Operation(summary = "Request sms verify for provided phone number.")
    @PostMapping("/sms/request")
    public ResponseResult<PhoneNumberSmsRequestResultStatus> phoneNumberSmsRequest(@RequestBody PhoneNumberSmsRequest request) throws IOException, ResponseFailureStatusException {

        if(ProjectBeanHolder.getPhoneNumberSmsVerifyService().requestSms(request.username(), request.phoneNumber(), request.type())) {

            return new ResponseResult<>(PhoneNumberSmsRequestResultStatus.SUCCESS);
        }

        return null;
    }

    @Operation(summary = "Verify sms code from record.")
    @PostMapping("/sms/verify")
    public ResponseResult<PhoneNumberSmsVerifyResultStatus> phoneNumberSmsVerify(@RequestBody PhoneNumberSmsVerify verify) throws ResponseFailureStatusException {

        if(ProjectBeanHolder.getPhoneNumberSmsVerifyService().verify(verify.username(), verify.code(), verify.type())) {

            return new ResponseResult<>(PhoneNumberSmsVerifyResultStatus.SUCCESS);
        }

        return null;
    }

    @Operation(summary = "Register an new account.", description = "")
    @PostMapping("/register")
    public ResponseResult<UserRegisterResultStatus> register(@RequestBody UserRegisterForm form) throws ResponseFailureStatusException {

        return form.getResult();
    }

    @HumanValidationRequired
    @Operation(summary = "auto complete given address.")
    @PostMapping("/autocomplete/address")
    public ResponseResult<GoogleApiRequestResultStatus> autocompleteAddress(HttpServletRequest request, @RequestBody Address addressRequest) throws ResponseFailureStatusException {

        GoogleAutocompletePredicted predicted = ProjectBeanHolder.getGoogleMapService().autocompleteRequest(request, null, addressRequest);

        if(predicted != null) {

            return new ResponseResult<>(GoogleApiRequestResultStatus.SUCCESS, predicted);
        }

        return null;
    }

    @HumanValidationRequired
    @Operation(summary = "get the address by place id")
    @PostMapping("/autocomplete/find-place-address")
    public ResponseResult<GoogleApiRequestResultStatus> autocompleteFindPlaceAddress(HttpServletRequest request, String placeId) {

        Address address = ProjectBeanHolder.getGoogleMapService().findPlaceAddressRequest(request, placeId);

        if(address != null) {

            return new ResponseResult<>(GoogleApiRequestResultStatus.SUCCESS, address);
        }

        return null;
    }

}
