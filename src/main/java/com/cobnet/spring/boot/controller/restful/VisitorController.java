package com.cobnet.spring.boot.controller.restful;

import com.cobnet.common.PuzzledImage;
import com.cobnet.exception.ResponseFailureStatusException;
import com.cobnet.interfaces.security.annotation.HumanValidationRequired;
import com.cobnet.security.AccountAuthenticationToken;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.cobnet.spring.boot.dto.support.*;
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
    public ResponseResult<HumanValidationValidateStatus> humanValidate(HttpServletRequest http, int position) throws ResponseFailureStatusException {

        if(ProjectBeanHolder.getHumanValidator().validate(http.getSession(true).getId(), position)) {

            return new ResponseResult<>(HumanValidationValidateStatus.SUCCESS);
        }

        return null;
    }

    @Operation(summary = "Check is cache human validated.")
    @GetMapping( "/visitor/human-validate")
    public boolean humanValidate(HttpServletRequest http) {

        return ProjectBeanHolder.getHumanValidator().isValidated(http.getSession(true).getId());
    }

    @Operation(summary = "Request new resources to verify is human operating.")
    @RequestMapping(method = RequestMethod.PATCH,value = "/visitor/human-validate")
    public ResponseResult<HumanValidationRequestStatus> humanValidate(HttpServletRequest http, HttpServletResponse response) throws IOException, ResponseFailureStatusException {

        PuzzledImage image = ProjectBeanHolder.getHumanValidator().create(http.getSession(true).getId());

        if(image != null) {

            return new ResponseResult<>(HumanValidationRequestStatus.SUCCESS, new ObjectWrapper<>("y-axis", image.getJigsawY()), new Base64Image(image.getImage(), "png"), new Base64Image(image.getJigsawImage(), "png"));
        }

        return null;
    }

    @HumanValidationRequired
    @Operation(summary = "Request sms verify for provided phone number.")
    @PostMapping("/visitor/sms/request")
    public ResponseResult<PhoneNumberSmsRequestResultStatus> phoneNumberSmsRequest(PhoneNumberSmsRequest request) throws IOException, ResponseFailureStatusException {

        if(ProjectBeanHolder.getPhoneNumberSmsVerifyService().requestSms(request.username(), request.phoneNumber(), request.type())) {

            return new ResponseResult<>(PhoneNumberSmsRequestResultStatus.SUCCESS);
        }

        return null;
    }

    @Operation(summary = "Verify sms code from record.")
    @PostMapping("/visitor/sms/verify")
    public ResponseResult<PhoneNumberSmsVerifyResultStatus> phoneNumberSmsVerify(PhoneNumberSmsVerify verify) throws ResponseFailureStatusException {

        if(ProjectBeanHolder.getPhoneNumberSmsVerifyService().verify(verify)) {

            return new ResponseResult<>(PhoneNumberSmsVerifyResultStatus.SUCCESS);
        }

        return null;
    }

    @Operation(summary = "Register an new account.", description = "")
    @PostMapping("/visitor/register")
    public ResponseResult<UserRegisterResultStatus> register(@RequestBody UserRegisterForm form) throws ResponseFailureStatusException {

        if(ProjectBeanHolder.getAccountService().register(form) != null) {

            return new ResponseResult<>(UserRegisterResultStatus.SUCCESS);
        }

        return null;
    }

    @HumanValidationRequired
    @Operation(summary = "auto complete given address.")
    @PostMapping("/visitor/autocomplete/address")
    public ResponseResult<GoogleApiRequestResultStatus> autocompleteAddress(HttpServletRequest request, @RequestBody AddressForm addressRequest) throws ResponseFailureStatusException {

        GoogleAutocompletePredicted predicted = ProjectBeanHolder.getGoogleMapService().autocompleteRequest(request, null, addressRequest);

        if(predicted != null) {

            return new ResponseResult<>(GoogleApiRequestResultStatus.SUCCESS, predicted);
        }

        return null;
    }

}
