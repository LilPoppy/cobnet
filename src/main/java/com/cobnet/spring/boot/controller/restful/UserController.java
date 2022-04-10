package com.cobnet.spring.boot.controller.restful;

import com.cobnet.common.PuzzledImage;
import com.cobnet.spring.boot.controller.support.OAuth2RegistryRepositoryHelper;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.cobnet.spring.boot.dto.support.HumanValidationFailureReason;
import com.cobnet.spring.boot.dto.support.HumanValidationRequireResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Tag(name = "User")
@RestController
public class UserController {

    @Operation(summary = "Login in default way.", description = "")
    @PostMapping("/user/login")
    public AuthenticationResult login(String username, String password, boolean rememberme) {

        System.out.println("HIIII");
        return null;
    }

    @Operation(summary = "Validate is human operating.")
    @PostMapping("/user/human/validate")
    public HumanValidationResult humanValidate(HttpServletRequest request, int movement) {

        return ProjectBeanHolder.getHumanValidator().imageValidate(request.getSession(true).getId(), movement);
    }

    @Operation(summary = "Request a new human validation.")
    @GetMapping("/user/human/create")
    public HumanValidationRequire createValidation(HttpServletRequest request) throws IOException {

        return ProjectBeanHolder.getHumanValidator().createImageValidation(request.getSession(true).getId());
    }

    @Operation(summary = "Register an new account.", description = "")
    @PostMapping("/user/register")
    public String register(HttpServletRequest request, RegisterForm form) {

        HumanValidationResult result = ProjectBeanHolder.getHumanValidator().imageValidate(request.getSession(true).getId(), form.getMovement());

        if(result.isSuccess() && result.getReason() == HumanValidationFailureReason.NONE) {


        }
        //TODO Verification code
        System.out.println(form);

        return "OK";
    }
}
