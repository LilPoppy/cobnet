package com.storechain.spring.boot.configuration;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.storechain.connection.handler.http.security.HttpAccessDeniedHandler;
import com.storechain.connection.handler.http.security.HttpAuthenticationFailureHandler;
import com.storechain.connection.handler.http.security.HttpAuthenticationSuccessHandler;
import com.storechain.connection.handler.http.security.HttpInvalidSessionHandler;
import com.storechain.interfaces.spring.repository.UserRoleRepository;
import com.storechain.interfaces.spring.repository.UserRepository;
import com.storechain.spring.boot.entity.User;
import com.storechain.spring.boot.entity.UserRole;
import com.storechain.utils.DatabaseManager;
import com.storechain.utils.SpringContext;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableWebSecurity(debug = false)
@Configuration
@ConfigurationProperties("security")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	final static String[] PERMIT_MATCHERS = { "/register", "/doRegister", "/login", "/doLogin" };

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

		return (UserDetailsService) SpringContext.getContext().getAutowireCapableBeanFactory()
				.getBean(UserRepository.class);
	}

	@Override
	protected void configure(HttpSecurity security) throws Exception {

		security.csrf().disable().formLogin().loginPage("/login").successHandler(new HttpAuthenticationSuccessHandler())
				.failureHandler(new HttpAuthenticationFailureHandler()).and().exceptionHandling()
				.accessDeniedHandler(new HttpAccessDeniedHandler()).and().authorizeRequests()
				.antMatchers(PERMIT_MATCHERS).permitAll().anyRequest().authenticated().and().logout()
				.logoutUrl("/logout").invalidateHttpSession(true).and().sessionManagement()
				.invalidSessionStrategy(new HttpInvalidSessionHandler()).maximumSessions(3);
	}

	public void configure(WebSecurity web) {
		web.ignoring().antMatchers(PERMIT_MATCHERS);
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
	final static class SimpleAuthenticationProvider implements AuthenticationProvider {

		@Autowired
		private UserRepository repository;

		private Object monitor = new Object();

		@Override
		public Authentication authenticate(Authentication authentication) throws AuthenticationException {

			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

			synchronized (monitor) {

				User user = (User) repository.loadUserByUsername(token.getName());

				if (user == null) {
					throw new UsernameNotFoundException("User is not exist.");
				}

				if (!user.getPassword().equals(token.getCredentials().toString())) {

					throw new BadCredentialsException("Wrong password");
				}

				return token;
			}
		}

		@Override
		public boolean supports(Class<?> authentication) {

			return UsernamePasswordAuthenticationToken.class.equals(authentication);
		}
	}

	@Configuration
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	public static class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

		@Override
		protected MethodSecurityExpressionHandler createExpressionHandler() {
			DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
			expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator());
			return expressionHandler;
		}
	}
	
	final static class CustomPermissionEvaluator implements PermissionEvaluator {

		@Override
		public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

			if ((authentication == null) || (targetDomainObject == null) || !(permission instanceof String)) {

				return false;
			}

			String targetType = targetDomainObject.getClass().getSimpleName().toUpperCase();

			return hasPrivilege(authentication, targetType, permission.toString().toUpperCase());
		}

		@Override
		public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {

			if ((authentication == null) || (targetType == null) || !(permission instanceof String)) {
				return false;
			}
			return hasPrivilege(authentication, targetType.toUpperCase(), permission.toString().toUpperCase());
		}

		private boolean hasPrivilege(Authentication authentication, String targetType, String permission) {
			
			System.out.println("authentication: " + authentication);
			System.out.println("targetType: " + targetType);
			System.out.println("permission: " + permission);
			for (GrantedAuthority grantedAuth : authentication.getAuthorities()) {
				if (grantedAuth.getAuthority().startsWith(targetType)
						&& grantedAuth.getAuthority().contains(permission)) {
					return true;
				}
			}
			return false;
		}

	}

}
