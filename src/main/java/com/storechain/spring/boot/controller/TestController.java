package com.storechain.spring.boot.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/index")
    public String index(){
        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "loginPage";
    }

    @PreAuthorize("hasPermission(#id, 'Foo', 'read')")
    @GetMapping("/register")
    public String register() {
        return "registerPage";
    }
}
