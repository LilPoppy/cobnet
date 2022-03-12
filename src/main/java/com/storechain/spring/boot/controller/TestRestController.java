package com.storechain.spring.boot.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.storechain.connection.handler.AuthenticationPacketHandler;
import com.storechain.interfaces.security.annotation.AccessSecured;
import com.storechain.spring.boot.configuration.SecurityConfiguration;
import com.storechain.utils.UserContextHolder;

@RestController
public class TestRestController {
	
	@Autowired
	private AuthenticationProvider provider;
	
	@PostMapping(value = "/authentication")
	public Map<String, Object> authentication(HttpServletRequest request, String username, String password) {
		
		HashMap<String, Object> result = new HashMap<>();
		
		result.put("result", false);
		
		username = username.trim();
		
		if(username == null ||username.isEmpty() || password == null || password.isEmpty()) {

			return result;
		}
		
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
		
    	try {
    		
            Authentication authentication = provider.authenticate(authRequest);
            
            result.put("result", true);
            
	        SecurityContextHolder.getContext().setAuthentication(authentication);
	        
	        HttpSession session = request.getSession();
	        
	        session.setAttribute(SecurityConfiguration.SESSION_KEY, SecurityContextHolder.getContext()); //redis context storage
	        
	        String token = UUID.randomUUID().toString();
	        
	        session.setAttribute(AuthenticationPacketHandler.AUTHENTICATION_TOKEN_KEY, token);

	        result.put("token", token);
	        
	        System.out.println("Successfully!" + authentication.getName());
    		

	    } catch (AuthenticationException ex) {
	    	
	    	System.out.println("Username or password is incorrect!");
	    }
    	
    	return result;
	}
	
	@PostMapping(value="/doLogin")
	public ModelAndView login(HttpServletRequest request, String username, String password) {

		ModelAndView view = new ModelAndView();
		
		Map<String, Object> result = authentication(request, username, password);
		
		view.addAllObjects(result);
		
		if((Boolean)result.get("result") == true) {
			
			view.setViewName("/index");
		} else {
			
			view.setViewName("/login");
		}

    	return view;
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
