package com.cobnet.spring.boot.configuration;

import com.cobnet.security.SecurityHandlerInterceptor;
import com.cobnet.spring.boot.controller.support.CustomOpenApiRequestService;
import com.cobnet.spring.boot.dto.support.ApplicationJsonMethodRequestResponseProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springdoc.core.GenericParameterService;
import org.springdoc.core.OperationService;
import org.springdoc.core.RequestBodyService;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springdoc.webmvc.core.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.List;
import java.util.Optional;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private List<HttpMessageConverter<?>> converters;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

        resolvers.add(new ApplicationJsonMethodRequestResponseProcessor(converters));
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(localeChangeInterceptorBean());
        registry.addInterceptor(securityHandlerInterceptorBean());
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptorBean() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Bean
    public SecurityHandlerInterceptor securityHandlerInterceptorBean() {

        return new SecurityHandlerInterceptor();
    }

    @Bean
    @Primary
    @Lazy(false)
    RequestService requestBuilder(GenericParameterService parameterBuilder, RequestBodyService requestBodyService,
                                  OperationService operationService, Optional<List<ParameterCustomizer>> parameterCustomizers,
                                  LocalVariableTableParameterNameDiscoverer localSpringDocParameterNameDiscoverer) {
        return new CustomOpenApiRequestService(parameterBuilder, requestBodyService,
                operationService, parameterCustomizers, localSpringDocParameterNameDiscoverer);
    }
}
