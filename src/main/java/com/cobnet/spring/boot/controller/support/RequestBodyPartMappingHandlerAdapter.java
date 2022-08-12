package com.cobnet.spring.boot.controller.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import javax.persistence.EntityManager;

public class RequestBodyPartMappingHandlerAdapter extends RequestMappingHandlerAdapter {

    private final ObjectMapper mapper;
    private final EntityManager manager;

    public RequestBodyPartMappingHandlerAdapter(ObjectMapper mapper, EntityManager manager) {

        this.mapper = mapper;
        this.manager = manager;
        SecurityContextHolder
    }

    @Override
    protected ServletInvocableHandlerMethod createInvocableHandlerMethod(HandlerMethod handlerMethod) {

        return new MappedServletInvocableHandlerMethod(this.mapper, this.manager, handlerMethod);
    }

}
