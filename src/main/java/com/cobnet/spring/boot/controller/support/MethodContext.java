package com.cobnet.spring.boot.controller.support;

import com.cobnet.interfaces.spring.controller.BodyPart;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.MethodParameter;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class MethodContext {

    private final BodyPartExpressionCompiler compiler;

    public MethodContext(BodyPartExpressionCompiler compiler) {

        this.compiler = compiler;
    }

    private final Map<String, BodyPart> infos = new HashMap<>();

    private final Map<String, ReferencableMethodParameter> parameters = new HashMap<>();

    public <T extends BodyPart> T getInfo(String name) {

        return (T) infos.get(name);
    }

    <T extends BodyPart> T getInfo(MethodParameter parameter) {

        if(parameter instanceof ReferencableMethodParameter referencable) {

            return getInfo(referencable.getName());
        }

        return null;
    }
}
