package com.cobnet.spring.boot.controller;

import com.cobnet.spring.boot.controller.support.OAuth2RegistryRepositoryHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class BasicController {

    @GetMapping("/visitor/login")
    public String login(HttpServletRequest request, Model model) {

        model.addAttribute("registrations", OAuth2RegistryRepositoryHelper.getRegistrationUrls());

        return "login.html";
    }

    @GetMapping("/index")
    public String index(HttpServletRequest request) {

        return "index.html";
    }

    @GetMapping("/index.js")
    public String indexJS(HttpServletRequest request) {

        return "index.js";
    }
}
