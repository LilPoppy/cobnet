package com.storechain.spring.boot.configuration;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import com.storechain.connection.handler.http.security.HttpAccessDeniedHandler;
import com.storechain.connection.handler.http.security.HttpOAuth2AuthenticationFailureHandler;
import com.storechain.connection.handler.http.security.HttpOAuth2AuthenticationSuccessHandler;
import com.storechain.connection.handler.http.security.HttpInvalidSessionHandler;
import com.storechain.connection.handler.http.security.HttpLogoutHandler;
import com.storechain.interfaces.annotation.AccessSecured;
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

	final static String[] PERMITTED_MATCHERS = { "/register", "/doRegister", "/login", "/doLogin", "/login/oauth2/code/google",  "/oauth2/authorization/google", "/userinfo"};
	
	private byte permissionDefaultPower;

	private String userDefaultRole;
	
	@Resource
	private Environment environment;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SessionRegistry getSessionRegistry() {

		SessionRegistry sessionRegistry = new SessionRegistryImpl();

		return sessionRegistry;
	}

	@Override
	@Bean
	public UserDetailsService userDetailsServiceBean() {

		return (UserDetailsService) DatabaseManager.getUserRepository();
	}

	@Override
	protected void configure(HttpSecurity security) throws Exception {
		
		security.csrf().disable()
				.authorizeRequests().antMatchers(PERMITTED_MATCHERS).permitAll().anyRequest().authenticated().and()
				.oauth2Login().userInfoEndpoint().oidcUserService(oidcUserService()).and().loginPage("/login").successHandler(new HttpOAuth2AuthenticationSuccessHandler()).failureHandler(new HttpOAuth2AuthenticationFailureHandler()).and()
				.exceptionHandling().accessDeniedHandler(new HttpAccessDeniedHandler()).and()
				.logout().logoutUrl("/logout").invalidateHttpSession(true).addLogoutHandler(new HttpLogoutHandler()).and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER).invalidSessionStrategy(new HttpInvalidSessionHandler()).maximumSessions(1);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		super.configure(auth);
	}
	
	private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
		final OidcUserService delegate = new OidcUserService();

		return (userRequest) -> {
			
			OidcUser oidcUser = delegate.loadUser(userRequest);

			String identity = "";
			
			if(oidcUser.getEmail() != null && oidcUser.getEmail().length() > 0 && oidcUser.getEmailVerified()) {
				
				identity = oidcUser.getEmail();
			}
			
			if(oidcUser.getPhoneNumber() != null && oidcUser.getPhoneNumber().length() > 0 && oidcUser.getPhoneNumberVerified()) {
				
				identity = oidcUser.getPhoneNumber();
			}
			
			oidcUser = new UserProvider(identity, userRequest.getClientRegistration().getClientId(), oidcUser.getName(), userRequest.getAccessToken().getTokenValue(),oidcUser.getIdToken().getTokenValue(), oidcUser.getAuthorities().stream().map(authority -> new UserProviderAuthority(authority.getAuthority())).toList(), new HashMap<>(oidcUser.getClaims()));
			
			return oidcUser;
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

			System.out.println("使用了Provider");
			
			if(authentication.getCredentials() != null) {

				User user = (User) DatabaseManager.getUserRepository().loadUserByUsername(authentication.getName());

				if (user == null) {
					throw new UsernameNotFoundException("User is not exist.");
				}

				if (!user.getPassword().equals(authentication.getCredentials().toString())) {

					throw new BadCredentialsException("Wrong password");
				}
				
				UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), user.getAuthorities());;
				
				token.setDetails(user);
				
				return token;
				
			}

			return authentication;
		}

		@Override
		public boolean supports(Class<?> authentication) {

			return UsernamePasswordAuthenticationToken.class.equals(authentication);
		}
	}

	@Aspect
	@Component
	static final class AccessSecureAspect {
		
	    @Around("@annotation(com.storechain.interfaces.annotation.AccessSecured)")
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
