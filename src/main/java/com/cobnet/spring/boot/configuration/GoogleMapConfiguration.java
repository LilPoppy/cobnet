package com.cobnet.spring.boot.configuration;

import com.google.maps.internal.ExceptionsAllowedToRetry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties("google.map")
public class GoogleMapConfiguration {

    private String apiKey;

    private String baseUrlOverride;

    private String channel;

    private String clientId;

    private String cryptographicSecret;

    private Duration errorTimeout;

    private Integer maxRetries;


    public String getApiKey() {
        return apiKey;
    }

    public String getBaseUrlOverride() {
        return baseUrlOverride;
    }

    public String getChannel() {
        return channel;
    }

    public String getClientId() {
        return clientId;
    }

    public String getCryptographicSecret() {
        return cryptographicSecret;
    }

    public Duration getErrorTimeout() {
        return errorTimeout;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setBaseUrlOverride(String baseUrlOverride) {
        this.baseUrlOverride = baseUrlOverride;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setCryptographicSecret(String cryptographicSecret) {
        this.cryptographicSecret = cryptographicSecret;
    }

    public void setErrorTimeout(Duration errorTimeout) {
        this.errorTimeout = errorTimeout;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }
}
