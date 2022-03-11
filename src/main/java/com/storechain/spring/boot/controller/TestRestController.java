package com.storechain.spring.boot.controller;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

import com.storechain.interfaces.security.annotation.AccessSecured;
import com.storechain.spring.boot.configuration.SecurityConfiguration;
import com.storechain.utils.UserContextHolder;

@RestController
public class TestRestController {
	
	@Autowired
	private AuthenticationProvider provider;
	
	@PostMapping("/doLogin")
	public ModelAndView login(HttpServletRequest request, String username, String password) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		username = username.trim();
		
        ModelAndView model = new ModelAndView();
        
    	model.setViewName("loginPage");
		
		if(username == null ||username.isEmpty() || password == null || password.isEmpty()) {
			return model;			
		}
    	
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
		
    	try {

            Authentication authentication = provider.authenticate(authRequest);
            
	        SecurityContextHolder.getContext().setAuthentication(authentication);
	        HttpSession session = request.getSession();
	        session.setAttribute(SecurityConfiguration.SESSION_KEY, SecurityContextHolder.getContext()); //redis context storage
	        
	    	model.setViewName("index");
	        model.addObject("message","User：" + authentication.getName());
	        model.addObject("ok",1);//success signed
	        System.out.println("Successfully!" + authentication.getName());
    		

	    } catch (AuthenticationException ex) {
	    	System.out.println("Username or password is incorrect!");
	    	model.addObject("message","Username or password is incorrect!");
	    	model.addObject("ok",0);//failed signed
	    }
    	
    	return model;
	}
	
    @PostMapping("/doRegister")
    public String register(String username, String password) {
    	
        return username.length() > 0 && password.length() > 0 ? "success" : "failed";
    }
    
    @AccessSecured(permissions = "user.test")
    @GetMapping("/test")
    public String test() {
    	System.out.println("success！");
    	
    	UserContextHolder.logout();
    	
    	return "success";
    }
    
    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public Map<String, Object> userinfo(Model model, Authentication authentication) {
    	
    	System.out.println("/userinfo");
    	System.out.println(authentication.getPrincipal());
  
        return Collections.singletonMap("name", authentication.getPrincipal().toString());
    }
    
    
    
    
}
