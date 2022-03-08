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

@RestController
public class TestRestController {
	
	@Autowired
	private AuthenticationProvider provider;
	
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;
    
	@Autowired
	private OAuth2AuthorizedClientService authorizedClientService;
	
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
	        session.setAttribute(SecurityConfiguration.SESSION_KEY, SecurityContextHolder.getContext()); // 这个非常重要，否则验证后将无法登陆
	        
	    	model.setViewName("index");
	        model.addObject("message","登录用户：" + authentication.getName());
	        model.addObject("ok",1);//这样view/customLogin.jsp得到成功标记后可以做url跳转。
	        System.out.println("登陆成功！" + authentication.getName());
    		

	    } catch (AuthenticationException ex) {
	    	System.out.println("用户名或密码错误！");
	    	model.addObject("message","用户名或密码错误");
	    	model.addObject("ok",0);//为了view/customLogin.jsp得到失败标记后可以提醒用户重新输入用户名、密码。
	    }
    	
    	return model;
	}
	
    @PostMapping("/doRegister")
    public String register(String username, String password) {
    	
        return username.length() > 0 && password.length() > 0 ? "注册成功" : "注册失败";
    }
    
    @AccessSecured(permissions = "user.test")
    @GetMapping("/test")
    public String test() {
    	System.out.println("成功！");
    	
    	return "success";
    }
    
    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public Map<String, Object> userinfo(Model model, Authentication authentication) {
    	
    	System.out.println("/userinfo");
    	System.out.println("验证主体：" + authentication);
    	
        return Collections.singletonMap("name", authentication.getPrincipal().toString());
    }
    
    
    
    
}
