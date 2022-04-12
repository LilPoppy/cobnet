package com.cobnet.spring.boot.configuration;

import com.cobnet.security.OAuth2LoginAccountAuthenticationFilter;
import com.cobnet.security.UserAuthenticationProvider;
import com.cobnet.security.UserDetailCheckFilter;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.session.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@EnableWebSecurity(debug = false)
@Configuration
@ConfigurationProperties("spring.security")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final static Logger LOG = LoggerFactory.getLogger(SecurityConfiguration.class);

    public final static String PREVIOUS_URL = "PREVIOUS_URL";

    public final static String CONNECTION_TOKEN = "CONNECTION_TOKEN";

    final static String[] PERMITTED_MATCHERS = { "/user/register" ,"/user/sms/verify", "/user/sms/request", "/user/human-validate/request", "/user/human-validate/validate", "/swagger-ui", "/oauth2/registration-urls", "/sms/reply" };

    private String usernameFormatRegex;

    private String passwordFormatRegex;

    private byte permissionDefaultPower;

    private boolean humanValidationEnable;

    private Duration humanValidationCreateInterval;

    private Duration humanValidationExpire;

    private boolean phoneNumberVerifyEnable;

    private Duration phoneNumberSmsGenerateInterval;

    private Duration phoneNumberSmsCodeExpire;

    private String phoneNumberVerifySmsMessage;

    private int phoneNumberMaxUse;

    private int emailMaxUse;

    private String userDefaultRole;

    private String loginPageUrl;

    private String authenticationUrl;

    private String loginSuccessUrl;

    private String loginFailureUrl;

    private String logoutUrl;

    private String logoutSuccessUrl;

    private String usernameParameter;

    private String passwordParameter;

    private String rememberMeParameter;

    private OAuth2Configuration oauth2;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private LogoutHandler logoutHandler;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PersistentTokenRepository persistentTokenRepository;

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

    @Bean
    public RememberMeServices rememberMeServicesBean(@Autowired UserDetailsService service, @Autowired PersistentTokenRepository repository) throws Exception {

        return new PersistentTokenBasedRememberMeServices("remember-me-service", service, repository);
    }

    @Override
    protected void configure(HttpSecurity security) throws Exception {

        security.csrf().disable()
                //authorize config
                .authorizeRequests().antMatchers(this.getPermittedMatchers()).permitAll().anyRequest().authenticated().and()
                //form login
                .formLogin().loginPage(this.getLoginPageUrl()).loginProcessingUrl(this.getAuthenticationUrl())
                .usernameParameter(this.getUsernameParameter()).passwordParameter(this.getPasswordParameter())
                .successHandler(authenticationSuccessHandler).failureHandler(authenticationFailureHandler).and()
                .userDetailsService(ProjectBeanHolder.getUserRepository())
                .authenticationProvider(authenticationProviderBean())
                .rememberMe().rememberMeParameter(this.getRememberMeParameter()).rememberMeServices(rememberMeServicesBean(userDetailsService, persistentTokenRepository)).key("uniqueAndSecret").tokenValiditySeconds(86400).and()
                .logout().logoutUrl(this.getLogoutUrl()).invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID").addLogoutHandler(logoutHandler).and()
                //oauth2
                .oauth2Login().loginPage(this.getLoginPageUrl())
                .successHandler(authenticationSuccessHandler).failureHandler(authenticationFailureHandler)
                .authorizationEndpoint().baseUri(this.getOauth2().getAuthenticationUrl()).and()
                .userInfoEndpoint().oidcUserService(ProjectBeanHolder.getExternalUserRepository()).and().and()
                //session
                .sessionManagement().sessionAuthenticationStrategy(compositeSessionAuthenticationStrategyBean()).sessionCreationPolicy(getSessionCreationPolicy()).maximumSessions(1).sessionRegistry(sessionRegistryBean());
                //filter
                security.addFilterBefore(userDetailCheckFilterBean(), UsernamePasswordAuthenticationFilter.class)
                        .addFilterBefore(oAuth2LoginAccountAuthenticationFilterBean(), OAuth2LoginAuthenticationFilter.class);
    }

    protected SessionCreationPolicy getSessionCreationPolicy() {

        return SessionCreationPolicy.IF_REQUIRED;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public UserDetailCheckFilter userDetailCheckFilterBean() {

        return new UserDetailCheckFilter(this.getAuthenticationUrl());
    }

    @Bean
    @DependsOn("autowireLoader")
    public OAuth2LoginAccountAuthenticationFilter oAuth2LoginAccountAuthenticationFilterBean() throws Exception {

        OAuth2LoginAccountAuthenticationFilter filter = new OAuth2LoginAccountAuthenticationFilter(ProjectBeanHolder.getClientRegistrationRepository(), ProjectBeanHolder.getOauth2AuthorizedClientService());
        filter.setAuthenticationManager(ProjectBeanHolder.getAuthenticationManager());
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(authenticationFailureHandler);
        filter.setApplicationEventPublisher(ProjectBeanHolder.getApplicationEventPublisher());
        SessionCreationPolicy policy = getSessionCreationPolicy();
        filter.setAllowSessionCreation(policy != SessionCreationPolicy.NEVER && policy != SessionCreationPolicy.STATELESS);
        filter.setFilterProcessesUrl(this.getOauth2().getRedirectUrl() + "/*");
        filter.setSessionAuthenticationStrategy(compositeSessionAuthenticationStrategyBean());
        filter.setRememberMeServices(rememberMeServicesBean(userDetailsService, persistentTokenRepository));

        return filter;
    }

    @Bean
    public CompositeSessionAuthenticationStrategy compositeSessionAuthenticationStrategyBean() {

        List<SessionAuthenticationStrategy> strategies = new ArrayList<>();

        strategies.add(new ConcurrentSessionControlAuthenticationStrategy(sessionRegistryBean()));
        strategies.add(new ChangeSessionIdAuthenticationStrategy());
        strategies.add(new RegisterSessionAuthenticationStrategy(sessionRegistryBean()));

        return new CompositeSessionAuthenticationStrategy(strategies);
    }

    @Bean
    public AuthenticationProvider authenticationProviderBean() {

        return new UserAuthenticationProvider();
    }

    public String[] getPermittedMatchers() {

        return Stream.concat(Arrays.stream(PERMITTED_MATCHERS), Stream.of(this.getLoginPageUrl(), this.getLoginFailureUrl(), this.getAuthenticationUrl(), this.getOauth2().getLoginFailureUrl(), this.getOauth2().getAuthenticationUrl() + "/*", this.getOauth2().getRedirectUrl() + "/*")).distinct().filter(url -> url != null && url.length() > 0).toArray(String[]::new);
    }

    public byte getPermissionDefaultPower() {
        return permissionDefaultPower;
    }

    public void setPermissionDefaultPower(byte authorityDefaultPower) {
        this.permissionDefaultPower = authorityDefaultPower;
    }

    public UserRole getUserDefaultAuthority() {

        Optional<UserRole> role = ProjectBeanHolder.getUserRoleRepository().findByRoleEqualsIgnoreCase(userDefaultRole);

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

    public String getUsernameFormatRegex() {
        return usernameFormatRegex;
    }

    public String getPasswordFormatRegex() {
        return passwordFormatRegex;
    }

    public Duration getHumanValidationCreateInterval() {
        return humanValidationCreateInterval;
    }

    public Duration getHumanValidationExpire() {
        return humanValidationExpire;
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

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public String getLogoutSuccessUrl() {
        return logoutSuccessUrl;
    }

    public String getUsernameParameter() {
        return usernameParameter;
    }

    public String getPasswordParameter() {
        return passwordParameter;
    }

    public String getRememberMeParameter() {
        return rememberMeParameter;
    }

    public boolean isPhoneNumberVerifyEnable() {
        return phoneNumberVerifyEnable;
    }

    public Duration getPhoneNumberSmsGenerateInterval() {
        return phoneNumberSmsGenerateInterval;
    }

    public Duration getPhoneNumberSmsCodeExpire() {
        return phoneNumberSmsCodeExpire;
    }

    public String getPhoneNumberVerifySmsMessage() {
        return phoneNumberVerifySmsMessage;
    }

    public int getPhoneNumberMaxUse() {
        return phoneNumberMaxUse;
    }

    public int getEmailMaxUse() {
        return emailMaxUse;
    }

    public boolean isHumanValidationEnable() {
        return humanValidationEnable;
    }

    public void setUsernameFormatRegex(String usernameFormatRegex) {
        this.usernameFormatRegex = usernameFormatRegex;
    }

    public void setPasswordFormatRegex(String passwordFormatRegex) {
        this.passwordFormatRegex = passwordFormatRegex;
    }

    public void setHumanValidationEnable(boolean humanValidationEnable) {
        this.humanValidationEnable = humanValidationEnable;
    }

    public void setHumanValidationCreateInterval(Duration humanValidationCreateInterval) {
        this.humanValidationCreateInterval = humanValidationCreateInterval;
    }

    public void setHumanValidationExpire(Duration humanValidationExpire) {
        this.humanValidationExpire = humanValidationExpire;
    }

    public void setPhoneNumberVerifyEnable(boolean phoneNumberVerifyEnable) {
        this.phoneNumberVerifyEnable = phoneNumberVerifyEnable;
    }

    public void setPhoneNumberSmsGenerateInterval(Duration phoneNumberSmsGenerateInterval) {
        this.phoneNumberSmsGenerateInterval = phoneNumberSmsGenerateInterval;
    }

    public void setPhoneNumberSmsCodeExpire(Duration phoneNumberSmsCodeExpire) {
        this.phoneNumberSmsCodeExpire = phoneNumberSmsCodeExpire;
    }

    public void setPhoneNumberVerifySmsMessage(String phoneNumberVerifySmsMessage) {
        this.phoneNumberVerifySmsMessage = phoneNumberVerifySmsMessage;
    }

    public void setPhoneNumberMaxUse(int phoneNumberMaxUse) {
        this.phoneNumberMaxUse = phoneNumberMaxUse;
    }


    public void setEmailMaxUse(int emailMaxUse) {
        this.emailMaxUse = emailMaxUse;
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

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public void setLogoutSuccessUrl(String logoutSuccessUrl) {
        this.logoutSuccessUrl = logoutSuccessUrl;
    }

    public void setUsernameParameter(String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        this.passwordParameter = passwordParameter;
    }

    public void setRememberMeParameter(String rememberMeParameter) {
        this.rememberMeParameter = rememberMeParameter;
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
