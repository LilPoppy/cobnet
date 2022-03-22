package com.cobnet.spring.boot.configuration;

import com.cobnet.connection.handler.http.HttpAuthenticationFailureHandler;
import com.cobnet.connection.handler.http.HttpAuthenticationSuccessHandler;
import com.cobnet.security.OAuth2LoginAccountAuthenticationFilter;
import com.cobnet.security.UserAuthenticationProvider;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@EnableWebSecurity(debug = false)
@Configuration
@ConfigurationProperties("spring.security")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final static Logger LOG = LoggerFactory.getLogger(SecurityConfiguration.class);

    public final static String PREVIOUS_URL = "PREVIOUS_URL";

    public final static String CONNECTION_TOKEN = "CONNECTION_TOKEN";

    final static String[] PERMITTED_MATCHERS = { "/register", "/checkRegistry", "/authenticate", "/login/oauth2/redirect/binding" };

    private byte permissionDefaultPower;

    private String userDefaultRole;

    private String loginPageUrl;

    private String authenticationUrl;

    private String loginSuccessUrl;

    private String loginFailureUrl;

    private String usernameParameter;

    private String passwordParameter;

    private OAuth2Configuration oauth2;

    @Autowired
    public ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @Bean
    public PasswordEncoder passwordEncoderBean() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SessionRegistry sessionRegistryBean() {

        return new SessionRegistryImpl();
    }

    /**
     *  Unable to pass in run time
     *     Caused by: java.lang.NullPointerException: null
     *      at org.springframework.util.ReflectionUtils.makeAccessible(ReflectionUtils.java:788) ~[na:na]
     *      at org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter$AuthenticationManagerDelegator.<init>(WebSecurityConfigurerAdapter.java:501) ~[na:na]
     *      at org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter.authenticationManagerBean(WebSecurityConfigurerAdapter.java:251) ~[cobnet:5.6.2]
     *      at com.cobnet.spring.boot.configuration.SecurityConfiguration.authenticationManagerBean(SecurityConfiguration.java:69) ~[cobnet:0.0.1-SNAPSHOT]
     *      at org.springframework.aot.ContextBootstrapInitializer.lambda$initialize$142(ContextBootstrapInitializer.java:634) ~[na:na]
     *      at org.springframework.aot.beans.factory.BeanDefinitionRegistrar$ThrowableSupplier.get(BeanDefinitionRegistrar.java:317) ~[na:na]
     *      at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.obtainFromSupplier(AbstractAutowireCapableBeanFactory.java:1249) ~[na:na]
     *      at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1191) ~[na:na]
     *      at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:582) ~[na:na]
     *      at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:542) ~[na:na]
     *      ... 31 common frames omitted
     */
//    @Override
//    @Bean
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }


    @Override
    protected void configure(HttpSecurity security) throws Exception {

        security.csrf().disable()
                //authorize config
                .authorizeRequests().antMatchers(this.getPermittedMatchers()).permitAll().anyRequest().authenticated().and()
                //form login
                .formLogin().loginPage(this.getLoginPageUrl()).loginProcessingUrl(this.getAuthenticationUrl()).failureUrl(this.getLoginFailureUrl())
                .usernameParameter(this.getUsernameParameter()).passwordParameter(this.getPasswordParameter())
                .successHandler(httpAuthenticationSuccessHandlerBean()).failureHandler(httpAuthenticationFailureHandlerBean()).and()
                .userDetailsService(ProjectBeanHolder.getUserRepository())
                //oauth2
                .oauth2Login().loginPage(this.getLoginPageUrl()).failureUrl(this.getOauth2().getLoginFailureUrl())
                .successHandler(this.httpAuthenticationSuccessHandlerBean()).failureHandler(this.httpAuthenticationFailureHandlerBean())
                .authorizationEndpoint().baseUri(this.getOauth2().getAuthenticationUrl()).and()
                .userInfoEndpoint().oidcUserService(ProjectBeanHolder.getExternalUserRepository()).and().and()
                //.authenticationProvider(oauth2UserAuthenticationProviderBean())
                //session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).maximumSessions(1).sessionRegistry(sessionRegistryBean()).and().and()
                //filter
                .addFilterBefore(new OAuth2LoginAccountAuthenticationFilter(null, null), OAuth2LoginAuthenticationFilter.class);


    }

    @Bean
    public AuthenticationSuccessHandler httpAuthenticationSuccessHandlerBean() {

        return new HttpAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler httpAuthenticationFailureHandlerBean() {

        return new HttpAuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationProvider authenticationProviderBean() {

        return new UserAuthenticationProvider();
    }

    public String[] getPermittedMatchers() {

        return Stream.concat(Arrays.stream(PERMITTED_MATCHERS),
                Arrays.stream(new String[]{this.getLoginPageUrl(), this.getLoginFailureUrl(), this.getAuthenticationUrl(), this.getOauth2().getLoginFailureUrl(), this.getOauth2().getAuthenticationUrl() + "/*", this.getOauth2().getRedirectUrl() + "/*"})).toArray(String[]::new);
    }

    public byte getPermissionDefaultPower() {
        return permissionDefaultPower;
    }

    public void setPermissionDefaultPower(byte authorityDefaultPower) {
        this.permissionDefaultPower = authorityDefaultPower;
    }

    public UserRole getUserDefaultAuthority() {

        Optional<UserRole> role =ProjectBeanHolder.getUserRoleRepository().findByRoleEqualsIgnoreCase(userDefaultRole);

        return role.orElse(null);
    }

    public void setUserDefaultRole(String userDefaultRole) {

        this.userDefaultRole = userDefaultRole;
    }

    public String getAuthenticationUrl() {

        return this.authenticationUrl;
    }

    public void setAuthenticationUrl(String url) {

        this.authenticationUrl = url;
    }

    public OAuth2Configuration getOauth2() {

        return this.oauth2;
    }

    public void setOauth2(OAuth2Configuration oauth2) {

        this.oauth2 = oauth2;
    }

    public String getUserDefaultRole() {
        return userDefaultRole;
    }

    public String getLoginPageUrl() {
        return loginPageUrl;
    }

    public String getLoginSuccessUrl() {
        return loginSuccessUrl;
    }

    public String getLoginFailureUrl() {
        return loginFailureUrl;
    }

    public String getUsernameParameter() {
        return usernameParameter;
    }

    public String getPasswordParameter() {
        return passwordParameter;
    }

    public void setLoginPageUrl(String loginPageUrl) {
        this.loginPageUrl = loginPageUrl;
    }

    public void setLoginSuccessUrl(String loginSuccessUrl) {
        this.loginSuccessUrl = loginSuccessUrl;
    }

    public void setLoginFailureUrl(String loginFailureUrl) {
        this.loginFailureUrl = loginFailureUrl;
    }

    public void setUsernameParameter(String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        this.passwordParameter = passwordParameter;
    }

    public static class OAuth2Configuration {

        private String authenticationUrl;

        private String redirectUrl;

        private String loginFailureUrl;

        public String getAuthenticationUrl() {
            return authenticationUrl;
        }

        public String getRedirectUrl() {
            return redirectUrl;
        }

        public String getLoginFailureUrl() {
            return loginFailureUrl;
        }

        public void setAuthenticationUrl(String authenticationUrl) {
            this.authenticationUrl = authenticationUrl;
        }

        public void setRedirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }

        public void setLoginFailureUrl(String loginFailureUrl) {
            this.loginFailureUrl = loginFailureUrl;
        }
    }

}
