package com.storechain.spring.boot.configuration;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.storechain.connection.handler.http.security.HttpAccessDeniedHandler;
import com.storechain.connection.handler.http.security.HttpOAuth2AuthenticationFailureHandler;
import com.storechain.connection.handler.http.security.HttpOAuth2AuthenticationSuccessHandler;
import com.storechain.connection.handler.http.security.HttpSessionAuthenticationHandler;
import com.storechain.interfaces.security.annotation.AccessSecured;
import com.storechain.connection.handler.http.security.HttpInvalidSessionHandler;
import com.storechain.connection.handler.http.security.HttpLogoutHandler;
import com.storechain.security.UserAuthenticationToken;
import com.storechain.spring.boot.entity.User;
import com.storechain.spring.boot.entity.UserProvider;
import com.storechain.spring.boot.entity.UserProviderAuthority;
import com.storechain.spring.boot.entity.UserRole;
import com.storechain.utils.DatabaseManager;
import com.storechain.utils.SpringContext;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@EnableWebSecurity(debug = false)
@Configuration
@ConfigurationProperties("spring.security")
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	public static final String PREVIOUS_URL = "previousUrl";
	
	public static final String SESSION_KEY = "SPRING_SECURITY_CONTEXT";
	
	public static final Class<?>[] REMOVED_FILTERS = { OAuth2LoginAuthenticationFilter.class };
	
	final static String[] PERMITTED_MATCHERS = { "/register", "/doRegister", "/login", "/doLogin", "/login/oauth2/code/google",  "/oauth2/authorization/google" };
	
	private byte permissionDefaultPower;

	private String userDefaultRole;

	@Resource
	private Environment environment;
	
	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;
	
	@Autowired
	private OAuth2AuthorizedClientService authorizedClientService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SessionRegistry getSessionRegistry() {

		SessionRegistry sessionRegistry = new SessionRegistryImpl();

		return sessionRegistry;
	}
	
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

	@Bean
	@Override
	public UserDetailsService userDetailsServiceBean() {

		return (UserDetailsService) DatabaseManager.getUserRepository();
	}

	

	
	@Override
	protected void configure(HttpSecurity security) throws Exception {
		//OAuth2AuthenticationToken

		security
			//csrf config
			.csrf().disable()
			//authorize config
			.authorizeRequests().antMatchers(PERMITTED_MATCHERS).permitAll().anyRequest().authenticated().and()
			//form login
			.formLogin().loginPage("/login").successHandler(new HttpOAuth2AuthenticationSuccessHandler()).failureHandler(new HttpOAuth2AuthenticationFailureHandler()).and()
			//oauth2 config
			.oauth2Login()
				//user
				.userInfoEndpoint().oidcUserService(oidcUserService()).and()
				//login
				.loginPage("/login").defaultSuccessUrl("/index").and()
			.addFilterBefore(new UserLoginAuthenticationFilter(this.clientRegistrationRepository, this.authorizedClientService, this.authenticationManager), OAuth2LoginAuthenticationFilter.class)
			//exception config
			.exceptionHandling().accessDeniedHandler(new HttpAccessDeniedHandler()).and()
			//logout config
			.logout().logoutUrl("/logout").deleteCookies("JSESSIONID").invalidateHttpSession(true).addLogoutHandler(new HttpLogoutHandler()).and()
			//session config
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER).invalidSessionStrategy(new HttpInvalidSessionHandler()).maximumSessions(1);
	}	


	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		super.configure(auth);
	}
	
	private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
		
		final OidcUserService delegate = new OidcUserService();
		
		return (request) -> {

			OidcUser user = delegate.loadUser(request);
			
			String identity = "";
			
			String provider = request.getClientRegistration().getRegistrationId();
			
			String email = user.getEmail();
			
			List<UserProviderAuthority> authorities = user.getAuthorities().stream().map(authority -> new UserProviderAuthority(authority.getAuthority())).toList();
			
			HashMap<String, Object> attributes = new HashMap<>(user.getAttributes());
			
			if(email != null && email.length() > 0 && user.getEmailVerified()) {
				
				identity = email;
			}
			
			String phone = user.getPhoneNumber();
			
			if(phone != null && phone.length() > 0 && user.getPhoneNumberVerified()) {
				
				identity = phone;
			}
			
			user = new UserProvider(identity, provider, request.getIdToken().getTokenValue(), request.getAccessToken().getTokenValue(), authorities, attributes);
			
		    UserProvider data = DatabaseManager.getUserProviderRepository().findByIdentityIgnoreCase(identity, provider);
			
			if(data == null || !user.equals(data)) {
				
				data = (UserProvider) user;
				
				if(data.getUser() != null) {
					
					DatabaseManager.getUserProviderRepository().save(data);
				}
			} 
				
			return data;
		};
	}

	public byte getPermissionDefaultPower() {
		return permissionDefaultPower;
	}

	public void setPermissionDefaultPower(byte authorityDefaultPower) {
		this.permissionDefaultPower = authorityDefaultPower;
	}

	public UserRole getUserDefaultAuthority() {

		return DatabaseManager.getUserRoleRepository().findByRoleIgnoreCase(userDefaultRole);
	}

	public void setUserDefaultRole(String userDefaultRole) {
		this.userDefaultRole = userDefaultRole;
	}

	
	@Component
	final static class UserAuthenticationProvider implements AuthenticationProvider {

		@Override
		public Authentication authenticate(Authentication authentication) throws AuthenticationException {

			if((authentication instanceof UsernamePasswordAuthenticationToken || authentication instanceof UserAuthenticationToken) && authentication.getCredentials() != null) {

				User user = (User) DatabaseManager.getUserRepository().loadUserByUsername(authentication.getName());
				
				if (user == null) {
					throw new UsernameNotFoundException("User is not exist.");
				}

				if (!user.getPassword().equals(authentication.getCredentials().toString())) {

					throw new BadCredentialsException("Wrong password");
				}
				
				return new UserAuthenticationToken(user, authentication.getCredentials());
			}
			
			

			return authentication;
		}

		@Override
		public boolean supports(Class<?> authentication) {

			return Arrays.stream(new Class<?>[]{ UsernamePasswordAuthenticationToken.class, UserAuthenticationToken.class }).anyMatch(clazz -> clazz.isAssignableFrom(authentication));
		}
	}
	
	static final class UserLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

		public static final String DEFAULT_FILTER_PROCESSES_URI = "/login/oauth2/code/*";

		private static final String AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE = "authorization_request_not_found";

		private static final String CLIENT_REGISTRATION_NOT_FOUND_ERROR_CODE = "client_registration_not_found";
		
		private ClientRegistrationRepository clientRegistrationRepository;

		private OAuth2AuthorizedClientRepository authorizedClientRepository;

		private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository = new HttpSessionOAuth2AuthorizationRequestRepository();

		private Converter<OAuth2LoginAuthenticationToken, UserAuthenticationToken> authenticationResultConverter = this::createAuthenticationResult;

		public UserLoginAuthenticationFilter(ClientRegistrationRepository repository, OAuth2AuthorizedClientService service, AuthenticationManager authenticationManager) {
			this(repository, service, authenticationManager, DEFAULT_FILTER_PROCESSES_URI);
		}

		public UserLoginAuthenticationFilter(ClientRegistrationRepository repository, OAuth2AuthorizedClientService service, AuthenticationManager authenticationManager, String filterProcessesUrl) {
			this(repository, new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(service), authenticationManager,filterProcessesUrl);
		}

		public UserLoginAuthenticationFilter(ClientRegistrationRepository clientRepo, OAuth2AuthorizedClientRepository authorizedRepo, AuthenticationManager authenticationManager, String filterProcessesUrl) {
			
			super(filterProcessesUrl);
			this.clientRegistrationRepository = clientRepo;
			this.authorizedClientRepository = authorizedRepo;
			this.setAuthenticationManager(authenticationManager);
			this.setAuthenticationSuccessHandler(new HttpOAuth2AuthenticationSuccessHandler());
			this.setAuthenticationFailureHandler(new HttpOAuth2AuthenticationFailureHandler());
			this.setSessionAuthenticationStrategy(new HttpSessionAuthenticationHandler());
			this.setRememberMeServices(new NullRememberMeServices());
		}

		@Override
		public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
			
			MultiValueMap<String, String> params = toMultiMap(request.getParameterMap());
			
			if (!isAuthorizationResponse(params)) {
				
				OAuth2Error oauth2Error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST);
				
				throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
			}
			
			OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestRepository.removeAuthorizationRequest(request, response);
			
			if (authorizationRequest == null) {
				
				OAuth2Error oauth2Error = new OAuth2Error(AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE);
				
				throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
			}
			
			String registrationId = authorizationRequest.getAttribute(OAuth2ParameterNames.REGISTRATION_ID);
			
			ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(registrationId);
			
			if (clientRegistration == null) {
				
				OAuth2Error oauth2Error = new OAuth2Error(CLIENT_REGISTRATION_NOT_FOUND_ERROR_CODE, "Client Registration not found with Id: " + registrationId, null);
				
				throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
			}
			
			String redirectUri = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request)).replaceQuery(null).build().toUriString();

			OAuth2AuthorizationResponse authorizationResponse = convert(params, redirectUri);
			
			Object details = this.authenticationDetailsSource.buildDetails(request);
			
			OAuth2LoginAuthenticationToken authenticationRequest = new OAuth2LoginAuthenticationToken(clientRegistration, new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse));
			authenticationRequest.setDetails(details);
			
			OAuth2LoginAuthenticationToken result = (OAuth2LoginAuthenticationToken) this.getAuthenticationManager().authenticate(authenticationRequest);
			
			UserAuthenticationToken token = this.authenticationResultConverter.convert(result);
			Assert.notNull(token, "authentication result cannot be null");
			token.setDetails(details);
			
			OAuth2AuthorizedClient client = new OAuth2AuthorizedClient(result.getClientRegistration(), token.getName(), result.getAccessToken(), result.getRefreshToken());

			this.authorizedClientRepository.saveAuthorizedClient(client, token, request, response);
			
			return token;
		}

		private UserAuthenticationToken createAuthenticationResult(OAuth2LoginAuthenticationToken authenticationResult) {
			
			return new UserAuthenticationToken((UserProvider)authenticationResult.getPrincipal());
		}
		
		private MultiValueMap<String, String> toMultiMap(Map<String, String[]> map) {
			
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>(map.size());
			
			map.forEach((key, values) -> {
				
				if (values.length > 0) {
					
					for (String value : values) {
						params.add(key, value);
					}
				}
			});
			
			return params;
		}
		
		boolean isAuthorizationResponse(MultiValueMap<String, String> request) {
			
			return isAuthorizationResponseSuccess(request) || isAuthorizationResponseError(request);
		}

		boolean isAuthorizationResponseSuccess(MultiValueMap<String, String> request) {
			
			return StringUtils.hasText(request.getFirst(OAuth2ParameterNames.CODE)) && StringUtils.hasText(request.getFirst(OAuth2ParameterNames.STATE));
		}
		
		boolean isAuthorizationResponseError(MultiValueMap<String, String> request) {
			
			return StringUtils.hasText(request.getFirst(OAuth2ParameterNames.ERROR)) && StringUtils.hasText(request.getFirst(OAuth2ParameterNames.STATE));
		}
		
		OAuth2AuthorizationResponse convert(MultiValueMap<String, String> request, String redirectUri) {
			
			String code = request.getFirst(OAuth2ParameterNames.CODE);
			String errorCode = request.getFirst(OAuth2ParameterNames.ERROR);
			String state = request.getFirst(OAuth2ParameterNames.STATE);
			
			if (StringUtils.hasText(code)) {
				
				return OAuth2AuthorizationResponse.success(code).redirectUri(redirectUri).state(state).build();
			}
			
			String errorDescription = request.getFirst(OAuth2ParameterNames.ERROR_DESCRIPTION);
			
			String errorUri = request.getFirst(OAuth2ParameterNames.ERROR_URI);

			return OAuth2AuthorizationResponse.error(errorCode).redirectUri(redirectUri).errorDescription(errorDescription).errorUri(errorUri).state(state).build();
		}
		
	}


	@Aspect
	@Component
	static final class AccessSecureAspect {
		
	    @Around("@annotation(com.storechain.interfaces.security.annotation.AccessSecured)")
	    public Object processMethodsAnnotatedWithAccessSecuredAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {

	        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
	        Method method = signature.getMethod();
	        
	        AccessSecured annotation = method.getAnnotation(AccessSecured.class);

	        String[] roles = annotation.roles();
	        String[] permissions = annotation.permissions();
	        
	        Object detail = SecurityContextHolder.getContext().getAuthentication().getDetails();

	        User user = annotation.user().length() > 0 || detail == null || !(detail instanceof User) ? DatabaseManager.getUserRepository().findByUsernameIgnoreCase((String) SpringContext.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), annotation.user().length() > 0 ? annotation.user() : (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal())) : (User) detail;
	        
	        if(user != null) {
	        	
		        if(roles.length > 0) {
		        	
			        if (user.hasRole(roles)) {
			        	
			            return joinPoint.proceed();
			            
			        } else {

			        	accessDenied();
			        	return null;
			        }
		        }
		        
		        if(permissions.length > 0) {
		        	
			        if (user.hasPermission(permissions)) {
			        	
			            return joinPoint.proceed();
			        } else {
			        	
			        	accessDenied();
			        	return null;
			        }
		        }
	        } else if(roles.length > 0 && permissions.length > 0) {
	        	
	        	accessDenied();
	        	return null;
	        }

	        
	        return joinPoint.proceed();
	    }
	    
	    private void accessDenied() {
	    	
	    	ServletRequest request = SpringContext.getCurrentRequest();
	    	
	    	if(request instanceof HttpServletRequest) {
	    		
	    		HttpServletResponse response = (HttpServletResponse) SpringContext.getCurrentResponse();
	    		
	    		try {
					new HttpAccessDeniedHandler().handle((HttpServletRequest) request, response, new AccessDeniedException("An exception occurred when getting access denied response."));
				} catch (IOException | ServletException e) {
					e.printStackTrace();
				}
	    		
	    		return;
	    	}
	    	
	    }
	}
}
