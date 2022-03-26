package com.cobnet.spring.boot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@ConfigurationProperties("twilio")
public class TwilioConfiguration {

    private String accountSid;

    private String authenticationToken;

    private Set<String> numbers;

    public String getAccountSid() {
        return accountSid;
    }

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public Set<String> getNumbers() {
        return numbers;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public void setNumbers(Set<String> numbers) {
        this.numbers = numbers;
    }
}
