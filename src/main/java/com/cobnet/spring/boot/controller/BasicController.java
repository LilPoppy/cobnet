package com.cobnet.spring.boot.controller;

import com.cobnet.spring.boot.controller.support.OAuth2RegistryRepositoryHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class BasicController {

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {

        model.addAttribute("urls", OAuth2RegistryRepositoryHelper.getRegistrationUrls());

        return "login";
    }

    @GetMapping("/index")
    public String index(HttpServletRequest request) {

        return "index";
    }
}
