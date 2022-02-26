package com.storechain.spring.boot.configuration;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.storechain.connection.handler.http.security.HttpAccessDeniedHandler;
import com.storechain.connection.handler.http.security.HttpAuthenticationFailureHandler;
import com.storechain.connection.handler.http.security.HttpAuthenticationSuccessHandler;
import com.storechain.connection.handler.http.security.HttpInvalidSessionHandler;
import com.storechain.interfaces.spring.repository.UserRepository;
import com.storechain.spring.boot.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableWebSecurity(debug = false)
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	final static String[] PERMIT_MATCHERS = {"/register", "/doRegister", "/login", "/doLogin"};

	@Autowired
	private ApplicationContext context;
	
    @Bean
    public PasswordEncoder passwordEncoder(){
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
    	
		return (UserDetailsService) context.getAutowireCapableBeanFactory().getBean(UserRepository.class);
    }

    @Override
    protected void configure(HttpSecurity security) throws Exception {
    	
    	security.csrf().disable()
    	.formLogin().loginPage("/login").successHandler(new HttpAuthenticationSuccessHandler()).failureHandler(new HttpAuthenticationFailureHandler()).and()
        .exceptionHandling().accessDeniedHandler(new HttpAccessDeniedHandler()).and()
    	.authorizeRequests().antMatchers(PERMIT_MATCHERS).permitAll().anyRequest().authenticated().and()
        .logout().logoutUrl("/logout").invalidateHttpSession(true).and()
        .sessionManagement().invalidSessionStrategy(new HttpInvalidSessionHandler()).maximumSessions(3);
    }
    
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(PERMIT_MATCHERS);
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }
    
    @Component
    final static class SimpleAuthenticationProvider implements AuthenticationProvider {

    	@Autowired
    	private UserRepository repository;
    	
    	private Object monitor = new Object();
    	
        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        	
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
            
            synchronized(monitor) {
            	
                User user = (User) repository.loadUserByUsername(token.getName());

	            if (user == null) {
	                throw new UsernameNotFoundException("找不到该用户");
	            }
                System.out.println("0@@@@@@@@" + user);
	            if(!user.getPassword().equals(token.getCredentials().toString())) {
	                System.out.println("1@@@@@@@@" + user);
	                throw new BadCredentialsException("密码错误");
	            }
                System.out.println("2@@@@@@@@" + user);
                return token;
            }
        }
     
        @Override
        public boolean supports(Class<?> authentication) {
        	
            return  UsernamePasswordAuthenticationToken.class.equals(authentication);
        }
    }
    
}
