package com.cobnet.spring.boot.controller.restful;

import com.cobnet.interfaces.security.annotation.AccessSecured;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User")
@RestController
public class UserController {

    @Operation(summary = "Logout user")
    @PostMapping("/user/logout")
    public void logout() {
        throw new RuntimeException("apidoc");
    }
}
