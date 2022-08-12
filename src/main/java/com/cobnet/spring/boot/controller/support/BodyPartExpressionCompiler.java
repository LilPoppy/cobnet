package com.cobnet.spring.boot.controller.support;

import com.cobnet.interfaces.spring.controller.BodyPart;
import com.cobnet.interfaces.spring.controller.BodyPartKey;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.dynalink.linker.support.TypeUtilities;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.VariableReference;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BodyPartExpressionCompiler {

    private final SpelExpressionParser parser = new SpelExpressionParser();

    private final StandardEvaluationContext context = new StandardEvaluationContext();

    private final ObjectMapper mapper;

    private JsonNode data;

    private final Map<String, Object> args = new HashMap<>();

    public BodyPartExpressionCompiler(ObjectMapper mapper) {

        this.mapper = mapper;
    }

    public StandardEvaluationContext getContext() {
        return this.context;
    }

    public SpelExpressionParser getParser() {
        return this.parser;
    }

    public JsonNode getData() {

        return this.data;
    }

    public void setData(JsonNode data) {

        this.data = data;
    }

    public Map<String, Object> getArgs() {

        return Collections.unmodifiableMap(this.args);
    }

    public Object addArgument(String name, Object value) {

        return this.args.put(name, value);
    }

    public boolean compile(BodyPartKey key) throws IOException {

        Class<?> clazz = BodyPartArgumentResolver.getType(key.getType());

        JsonNode data = this.data.deepCopy();

        String name = key.getBodyPart().getName();

        if(key.getBodyPart().isIsolated()) {

            data = data.get(name);
        }

        if(data == null) {

            if(key.getBodyPart().isRequired()) {

                throw new MissingFormatArgumentException(String.format("Unable to find field of '%s' in json data!", name));
            }

            return false;
        }

        Expression expression = parser.parseExpression(key.getKey());

        if(expression instanceof SpelExpression spelExpression) {

            Set<String> references = BodyPartExpressionCompiler.getReferences(this.args, spelExpression.getAST());
            System.out.println(references);
            for(String reference : references) {

                reference = reference.substring(1);

                if(this.args.containsKey(reference)) {

                    Object referenced = this.args.get(reference);
                    System.out.println(reference + ": " + referenced);
                    if(reference == null) {

                        if(!key.isNullable()) {

                            throw new MissingFormatArgumentException(String.format("Unable to compile argument of '%s', because referenced argument '%s' is null.", key.getBodyPart().getName(), reference));
                        }

                        return false;
                    }

                    this.context.setVariable(reference, referenced);

                    continue;
                }

                JsonNode field = data.get(reference);

                if(field == null) {

                    if(!key.isNullable()) {

                        throw new MissingFormatArgumentException(String.format("Unable to compile argument of '%s', because required key of '%s' is missing in transmission.", key.getBodyPart().getName(), reference));
                    }

                    return false;
                }

                if(TypeUtilities.isMethodInvocationConvertible(clazz, Collection.class)) {

                    Collection elements = null;

                    if(TypeUtilities.isMethodInvocationConvertible(clazz, Set.class)) {

                        elements = new HashSet<>();
                    }

                    if(TypeUtilities.isMethodInvocationConvertible(clazz, List.class)) {

                        elements = new ArrayList<>();
                    }

                    if(!field.isArray()) {

                        throw new MissingFormatArgumentException(String.format("Unable to compile argument of '%s', because key of '%s' is not an array.", key.getBodyPart().getName(), reference));
                    }

                    Iterator<JsonNode> it = field.elements();

                    if(key.getType() instanceof ParameterizedType parameterized) {

                        clazz = (Class<?>) parameterized.getActualTypeArguments()[0];

                        while (it.hasNext()) {

                            JsonNode element = it.next();

                            elements.add(mapper.readValue(element.traverse(), clazz));
                        }
                    }

                    this.context.setVariable(reference, elements);

                    continue;
                }

                this.context.setVariable(reference, mapper.readValue(field.traverse(), clazz));
            }

        }

        key.setValue(expression.getValue(this.context, clazz));
        System.out.println(key.getValue());
        return true;
    }

    public String[] getReferences(BodyPartKey key) {

        Expression expression = parser.parseExpression(key.getKey());

        if(expression instanceof SpelExpression spel) {

            Set<String> references = BodyPartExpressionCompiler.getReferences(this.args, spel.getAST());

            return references.stream().filter(reference -> {

                reference = reference.substring(1);

                return this.args.containsKey(reference);

            }).toArray(String[]::new);
        }

        return new String[0];
    }

    public String[] getReferences(BodyPart part) {

        return Arrays.stream(part.getKeys()).map(key -> getReferences(key)).filter(Objects::nonNull).flatMap(Stream::of).toArray(String[]::new);
    }

    static Set<String> getReferences(Map<String, Object> referenced, SpelNode node) {

        int count = node.getChildCount();

        if(count > 0) {

            Set<String> result = new HashSet<>();

            for (int i = 0; i < count; i++) {

                SpelNode child = node.getChild(i);

                result = BodyPartExpressionCompiler.resolveGetKeys(result, referenced, child);
            }

            return result;
        }

        return BodyPartExpressionCompiler.resolveGetKeys(new HashSet<>(), referenced, node);
    }

    private static Set<String> resolveGetKeys(Set<String> set, Map<String, Object> referenced, SpelNode node) {

        if(node.getChildCount() > 0) {

            set.addAll(BodyPartExpressionCompiler.getReferences(referenced, node));
        }

        if(node instanceof VariableReference reference) {

            String ast = reference.toStringAST();

            set.add(ast);
        }

        return set;
    }
}
