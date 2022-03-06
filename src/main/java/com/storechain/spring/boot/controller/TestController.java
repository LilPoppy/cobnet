package com.storechain.spring.boot.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

@Controller
public class TestController {

    private static String authorizationRequestBaseUri = "oauth2/authorization";
    Map<String, String> oauth2AuthenticationUrls = new HashMap<>();
  
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;
    
	@Autowired
	private OAuth2AuthorizedClientService authorizedClientService;
    
    @GetMapping("/index")
    public String index(){
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model){
    	
        Iterable<ClientRegistration> clientRegistrations = null;
        
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository).as(Iterable.class);
        
        if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
        	
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        }

        clientRegistrations.forEach(registration -> oauth2AuthenticationUrls.put(registration.getClientName(), authorizationRequestBaseUri + "/" + registration.getRegistrationId()));
        model.addAttribute("urls", oauth2AuthenticationUrls);
        
        return "loginPage";
    }

    //@PreAuthorize("hasPermission(#id, 'Foo', 'read')")
    @GetMapping("/register")
    public String register() {
        return "registerPage";
    }
    
	@RequestMapping("/userinfo")
	public String userinfo(Model model, OAuth2AuthenticationToken authentication) {

		OAuth2AuthorizedClient client = this.authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(),authentication.getName());
		
		OAuth2AccessToken accessToken = client.getAccessToken();

		String userInfoEndpointUri = client.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();
		if (!StringUtils.isEmpty(userInfoEndpointUri)) {
		    RestTemplate restTemplate = new RestTemplate();
		    HttpHeaders headers = new HttpHeaders();
		    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
		      .getTokenValue());
		    HttpEntity<HttpHeaders> entity = new HttpEntity<HttpHeaders>(headers);
		    ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
		    Map userAttributes = response.getBody();
		    model.addAttribute("name", userAttributes.get("name"));
		}

		return "userinfo";
	}


    
}
