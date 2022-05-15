package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.List;


public class ApplicationJsonMethodRequestResponseProcessor extends RequestResponseBodyMethodProcessor {


    public ApplicationJsonMethodRequestResponseProcessor(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    public ApplicationJsonMethodRequestResponseProcessor(List<HttpMessageConverter<?>> converters, ContentNegotiationManager manager) {
        super(converters, manager);
    }

    public ApplicationJsonMethodRequestResponseProcessor(List<HttpMessageConverter<?>> converters, List<Object> requestResponseBodyAdvice) {
        super(converters, requestResponseBodyAdvice);
    }

    public ApplicationJsonMethodRequestResponseProcessor(List<HttpMessageConverter<?>> converters, ContentNegotiationManager manager, List<Object> requestResponseBodyAdvice) {
        super(converters, manager, requestResponseBodyAdvice);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        return ApplicationJson.class.isAssignableFrom(parameter.getParameter().getType());
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {

        return ApplicationJson.class.isAssignableFrom (returnType.getParameter().getClass());
    }
}
