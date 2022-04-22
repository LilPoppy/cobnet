package com.cobnet.spring.boot.configuration;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;

@Configuration
@ConfigurationProperties("google.console")
public class GoogleConsoleConfiguration {

    private String apiKey;

    private String credentials;

    private GoogleMapConfiguration map;

    public String getApiKey() {
        return apiKey;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public GoogleMapConfiguration getMap() {
        return map;
    }

    public void setMap(GoogleMapConfiguration map) {
        this.map = map;
    }

    public static class GoogleMapConfiguration {

        private String baseUrlOverride;

        private String channel;

        private String clientId;

        private String cryptographicSecret;

        private Duration errorTimeout;

        private Integer maxRetries;

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
}
