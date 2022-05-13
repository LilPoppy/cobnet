package com.cobnet.spring.boot.controller.restful;

import com.cobnet.interfaces.security.annotation.AccessSecured;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User")
@RestController
@RequestMapping("/user")
public class UserController {

    @Operation(summary = "Logout user")
    @PostMapping("/logout")
    public void logout() {
        throw new RuntimeException("apidoc");
    }
}
