package com.storechain.spring.boot.configuration;

import java.io.IOException;
import java.lang.reflect.Method;

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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.storechain.connection.handler.http.security.HttpAccessDeniedHandler;
import com.storechain.connection.handler.http.security.HttpAuthenticationFailureHandler;
import com.storechain.connection.handler.http.security.HttpAuthenticationSuccessHandler;
import com.storechain.connection.handler.http.security.HttpInvalidSessionHandler;
import com.storechain.connection.handler.http.security.HttpLogoutHandler;
import com.storechain.interfaces.annotation.AccessSecured;
import com.storechain.spring.boot.entity.User;
import com.storechain.spring.boot.entity.UserRole;
import com.storechain.utils.DatabaseManager;
import com.storechain.utils.SpringContext;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@EnableWebSecurity(debug = false)
@Configuration
@ConfigurationProperties("security")
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	final static String[] PERMITTED_MATCHERS = { "/register", "/doRegister", "/login", "/doLogin" };

	private byte permissionDefaultPower;

	private String userDefaultRole;

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
		
		security.csrf().disable().formLogin().loginPage("/login").successHandler(new HttpAuthenticationSuccessHandler())
				.failureHandler(new HttpAuthenticationFailureHandler()).and().exceptionHandling()
				.accessDeniedHandler(new HttpAccessDeniedHandler()).and()
				.authorizeRequests().antMatchers(PERMITTED_MATCHERS).permitAll().anyRequest().authenticated().and()
				.logout().logoutUrl("/logout").invalidateHttpSession(true).logoutSuccessUrl("/").addLogoutHandler(new HttpLogoutHandler()).and()
				.sessionManagement().invalidSessionStrategy(new HttpInvalidSessionHandler()).maximumSessions(1);
	}

	public void configure(WebSecurity web) {
		web.ignoring().antMatchers(PERMITTED_MATCHERS);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		super.configure(auth);
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
