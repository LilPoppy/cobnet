package com.storechain.spring.boot.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.storechain.spring.boot.configuration.SecurityConfiguration;

@Controller
public class TestController {

    private static String authorizationRequestBaseUri = "oauth2/authorization";
    Map<String, String> oauth2AuthenticationUrls = new HashMap<>();
  
    @Autowired
    private ClientRegistrationRepository oauth2RegistrationRepository;
    
    @GetMapping("/index")
    public String index(){
        return "index";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
    	
    	System.out.println("session：" + request.getSession().getId());
    	
    	String redirect = request.getHeader("Referer");
    	
    	request.getSession().setAttribute(SecurityConfiguration.PREVIOUS_URL, redirect == null || redirect.length() == 0 ? request.getRequestURL().toString() : redirect);
    	
        Iterable<ClientRegistration> registrations = null;
        
        ResolvableType type = ResolvableType.forInstance(oauth2RegistrationRepository).as(Iterable.class);
        
        if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
        	
            registrations = (Iterable<ClientRegistration>) oauth2RegistrationRepository;
        }

        registrations.forEach(registration -> oauth2AuthenticationUrls.put(registration.getClientName(), authorizationRequestBaseUri + "/" + registration.getRegistrationId()));
        
        model.addAttribute("urls", oauth2AuthenticationUrls);
        
        return "loginPage";
    }

    @GetMapping("/register")
    public String register() {
        return "registerPage";
    }
    



    
}
