package com.cobnet.spring.boot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

//TODO ssh, ftp etc remote supports
@Configuration
@ConfigurationProperties("file-source")
public class FileSourceConfiguration {

    private String url;

    public URL getUrl() throws MalformedURLException {
        return new URL(url);
    }

    public void setUrl(String url) throws MalformedURLException {
        this.url = url;
    }
}
