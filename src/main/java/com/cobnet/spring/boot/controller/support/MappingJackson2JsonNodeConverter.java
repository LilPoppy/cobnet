package com.cobnet.spring.boot.controller.support;

import com.cobnet.common.JPAUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.AbstractJsonHttpMessageConverter;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

public class MappingJackson2JsonNodeConverter extends AbstractJsonHttpMessageConverter {

    private final ObjectMapper mapper;

    public MappingJackson2JsonNodeConverter(ObjectMapper mapper) {
        super();
        this.mapper = mapper;
    }

    @Override
    protected Object readInternal(Type resolvedType, Reader reader) throws Exception {

        BodyPartExpressionCompiler compiler = MethodContextHolder.getContext().getCompiler();

        if(compiler.getData() == null) {

            compiler.setData(mapper.readTree(reader));
        }

        return compiler.getData();
    }

    @Override
    protected void writeInternal(Object object, Type type, Writer writer) throws Exception {

    }
}
