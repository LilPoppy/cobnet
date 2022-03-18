package com.cobnet.spring.boot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

//TODO add mybatis supports
@Configuration
@ConfigurationProperties("spring.datasource")
@EnableJpaRepositories(value = "com.cobnet.interfaces.spring.repository")
public class DatasourceConfiguration {

    private String driverClassName;

    private String url;

    private String username;

    private String password;

    /**
     * TODO Unsupported in native run
     * org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'dataSourceScriptDatabaseInitializer':
     * Unsatisfied dependency expressed through method 'dataSourceScriptDatabaseInitializer' parameter 0;
     * nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'dataSourceBean':
     * Unexpected exception during bean creation; nested exception is java.lang.IllegalStateException: No supported DataSource type found
     */
//    @Bean
//    public DataSource dataSourceBean() {
//
//        return DataSourceBuilder.create().driverClassName(this.getDriverClassName()).url(this.getUrl()).username(this.getUsername()).password(this.getPassword()).build();
//    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
