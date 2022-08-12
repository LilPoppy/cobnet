package com.cobnet.spring.boot.configuration;

import com.cobnet.security.SecurityHandlerInterceptor;
import com.cobnet.spring.boot.controller.support.CustomOpenApiRequestService;
import com.cobnet.spring.boot.controller.support.BodyPartArgumentResolver;
import com.cobnet.spring.boot.controller.support.MappingJackson2JsonNodeConverter;
import com.cobnet.spring.boot.controller.support.RequestBodyPartMappingHandlerAdapter;
import com.cobnet.spring.boot.dto.support.ApplicationJsonMethodRequestResponseProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springdoc.core.GenericParameterService;
import org.springdoc.core.OperationService;
import org.springdoc.core.RequestBodyService;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springdoc.webmvc.core.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
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
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired(required = false)
    private CacheManager cacheManager;

    @Autowired
    private List<HttpMessageConverter<?>> converters;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        WebMvcConfigurer.super.configureMessageConverters(converters);
        converters.add(0, new MappingJackson2JsonNodeConverter(mapper));
        this.converters = converters;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

        resolvers.add(new ApplicationJsonMethodRequestResponseProcessor(converters));
        resolvers.add(0, new BodyPartArgumentResolver(mapper, entityManager, cacheManager, converters));
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
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
    public RequestMappingHandlerAdapter servletInvocableHandlerMethodBean(RequestMappingHandlerAdapter adapter) {

        RequestMappingHandlerAdapter result = new RequestBodyPartMappingHandlerAdapter(this.mapper, this.entityManager);

        result.setArgumentResolvers(adapter.getArgumentResolvers().stream().sorted((x, y) -> {

            if(x instanceof BodyPartArgumentResolver) {
                return -1;
            }

            if(y instanceof BodyPartArgumentResolver) {
                return 1;
            }

            return 0;

        }).toList());
        result.setMessageConverters(adapter.getMessageConverters());
        result.setCustomArgumentResolvers(adapter.getCustomArgumentResolvers());
        result.setCustomReturnValueHandlers(adapter.getCustomReturnValueHandlers());
        result.setModelAndViewResolvers(adapter.getModelAndViewResolvers());
        result.setReactiveAdapterRegistry(adapter.getReactiveAdapterRegistry());
        result.setReturnValueHandlers(adapter.getReturnValueHandlers());
        result.setInitBinderArgumentResolvers(adapter.getInitBinderArgumentResolvers());
        result.setWebBindingInitializer(adapter.getWebBindingInitializer());
        result.setCacheControl(adapter.getCacheControl());
        result.setCacheSeconds(adapter.getCacheSeconds());
        result.setOrder(adapter.getOrder());
        result.setVaryByRequestHeaders(adapter.getVaryByRequestHeaders());
        result.setSupportedMethods(adapter.getSupportedMethods());
        result.setApplicationContext(adapter.getApplicationContext());

        return result;
    }

    @Bean
    @Primary
    @Lazy(false)
    public RequestService requestBuilder(GenericParameterService parameterBuilder, RequestBodyService requestBodyService,
                                  OperationService operationService, Optional<List<ParameterCustomizer>> parameterCustomizers,
                                  LocalVariableTableParameterNameDiscoverer localSpringDocParameterNameDiscoverer) {
        return new CustomOpenApiRequestService(parameterBuilder, requestBodyService,
                operationService, parameterCustomizers, localSpringDocParameterNameDiscoverer);
    }

}
