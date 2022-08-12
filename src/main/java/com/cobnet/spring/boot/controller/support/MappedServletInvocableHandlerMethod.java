package com.cobnet.spring.boot.controller.support;

import com.cobnet.common.JPAUtils;
import com.cobnet.common.SortUtils;
import com.cobnet.connection.support.Packet;
import com.cobnet.interfaces.spring.controller.BodyPart;
import com.cobnet.interfaces.spring.controller.annotations.EntityVariable;
import com.cobnet.interfaces.spring.controller.annotations.RequestBodyPart;
import com.cobnet.spring.boot.controller.support.enums.ComparisonOperator;
import com.cobnet.spring.boot.controller.support.enums.ConjunctiveOperator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import org.hibernate.metamodel.model.domain.internal.AbstractPluralAttribute;
import org.hibernate.metamodel.model.domain.spi.SimpleTypeDescriptor;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.expression.spel.SpelNode;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.metamodel.Type;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class MappedServletInvocableHandlerMethod extends ServletInvocableHandlerMethod {

    private final EntityManager manager;

    private final MethodParameter[] parameters;

    public MappedServletInvocableHandlerMethod(ObjectMapper mapper, EntityManager manager, Object handler, Method method) {
        super(handler, method);
        MethodContextHolder.context.set(new MethodContext(new BodyPartExpressionCompiler(mapper)));
        this.manager = manager;
        this.parameters = this.initMethodParameters();
    }

    public MappedServletInvocableHandlerMethod(ObjectMapper mapper, EntityManager manager, Object handler, Method method, MessageSource messageSource) {
        super(handler, method, messageSource);
        MethodContextHolder.context.set(new MethodContext(new BodyPartExpressionCompiler(mapper)));
        this.manager = manager;
        this.parameters = this.initMethodParameters();
    }

    public MappedServletInvocableHandlerMethod(ObjectMapper mapper, EntityManager manager, HandlerMethod handlerMethod) {
        super(handlerMethod);
        MethodContextHolder.context.set(new MethodContext(new BodyPartExpressionCompiler(mapper)));
        this.manager = manager;
        this.parameters = this.initMethodParameters();
    }

    private MethodParameter[] initMethodParameters() {

        Map<MethodParameter, ReferencableMethodParameter> map = Arrays.stream(super.getMethodParameters()).map(parameter -> new AbstractMap.SimpleEntry<>(parameter, this.resolveParameter(parameter))).filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for(ReferencableMethodParameter parameter : map.values()) {

            BodyPart part = MethodContextHolder.getContext().getInfos().get(parameter.getName());
            parameter.setReferenced(Arrays.stream(part.getReferenced()).map(referenced -> MethodContextHolder.getContext().getParameters().get(referenced.getName())).toArray(MethodParameter[]::new));
        }

        return SortUtils.topologicalSort(Arrays.stream(super.getMethodParameters()), parameter -> {

            ReferencableMethodParameter referencable = map.get(parameter);

            if(referencable == null) {

                return new ArrayList<>();
            }

            return Arrays.stream(referencable.getReferenced()).filter(referenced -> referenced != referencable).toList();

        }).stream().map(parameter -> {

            MethodParameter result = map.get(parameter);

            if(result != null) {

                return result;
            }

            return parameter;

        }).toArray(MethodParameter[]::new);
    }

    @Override
    public MethodParameter[] getMethodParameters() {

        return this.parameters;
    }

    protected ReferencableMethodParameter resolveParameterReference(MethodParameter parameter) {

        BodyPart part = MethodContextHolder.getContext().getInfo(parameter);

        if(part == null) {

            throw new IllegalArgumentException("No body part found for parameter " + parameter.getParameterName());
        }

        if(part instanceof EntityVariableInfo<?> info) {


        }

        return MethodContextHolder.getContext().getParameters().get(part.getName());
    }

    protected ReferencableMethodParameter resolveParameter(MethodParameter parameter) {

        //Make sure there is not a request body.
        if(!parameter.hasParameterAnnotation(RequestBody.class)) {

            RequestBodyPart part = parameter.getParameterAnnotation(RequestBodyPart.class);

            BodyPart body = null;

            if (part == null) {

                Class<?> type = MappedServletInvocableHandlerMethod.getEntityType(parameter);

                if (type != null) {
                    //要在这里之前设置 args MethodContextHolder.getContext().getCompiler().addArgument(body.getName(), null);
                    body = resolveEntityVariable(type, parameter);
                }

            } else {

                //TODO resolveRequestBodyPart
                //get info from RequestBodyPart
            }

            System.out.println(body);
            if(body != null) {

                MethodContextHolder.getContext().getInfos().put(body.getName(), body);
                ReferencableMethodParameter referencable = new ReferencableMethodParameter(body.getName(), parameter);
                MethodContextHolder.getContext().getParameters().put(body.getName(), referencable);

                return referencable;
            }
        }

        return null;
    }

    @Override
    public Object invokeForRequest(NativeWebRequest request, ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {

        Object[] args = this.getMethodArgumentValues(request, mavContainer, providedArgs);

        List<Map.Entry<Object, MethodParameter>> result = new ArrayList<>();

        MethodParameter[] parameters = this.getMethodParameters();

        for(int i = 0; i < args.length; i++) {

            result.add(new AbstractMap.SimpleEntry<>(args[i], parameters[i]));
        }

        args = result.stream().sorted(Comparator.comparingInt(x -> x.getValue().getParameterIndex())).map(pair -> pair.getKey()).toArray(Object[]::new);

        if (logger.isTraceEnabled()) {

            logger.trace("Arguments: " + Arrays.toString(args));
        }

        return this.doInvoke(args);
    }

    protected <T> EntityVariableInfo<T> resolveEntityVariableReference(EntityVariableInfo<T> info) {


    }

    protected <T> EntityVariableInfo<T> resolveEntityVariable(Class<T> type, MethodParameter parameter) {

        EntityVariable variable = parameter.getParameterAnnotation(EntityVariable.class);

        EntityVariableInfo<T> info = null;

        if (variable != null) {

            info = new EntityVariableInfo(type, variable);
        }

        if(info == null) {

            info = new EntityVariableInfo.Builder<T>().setType(type).setRequired(true).setIsolated(true).setCreatable(false).setCacheable(true).build();
        }

        if(info.getName() == null || info.getName().length() == 0) {

            info.setName(parameter.getParameter().getName());
        }

        if(info.getFlush() == null) {

            info.setFlush(FlushModeType.COMMIT);
        }

        if(info.getConditions() == null || info.getConditions().length == 0) {

            info.setConditions(new EntityAttributeConditionInfo.Builder().build());
        }

        for(int i = 0; i < info.getConditions().length; i++) {

            EntityAttributeConditionInfo condition = info.getConditions()[i];

            if(condition.getAttributes() == null || condition.getAttributes().length == 0) {

                condition.setAttributes(new EntityAttributeInfo.Builder().build());
            }

            for(int j = 0; j < condition.getAttributes().length; j++) {

                EntityAttributeInfo attribute = condition.getAttributes()[j];

                if(attribute.getAttribute() == null || attribute.getAttribute().length() == 0) {

                    attribute.setAttribute(JPAUtils.getId(this.manager, type));
                }

                if(attribute.getType() == null) {

                    attribute.setType(JPAUtils.getAttributeType(manager, info.getType(), attribute.getAttribute()));
                }

                if(attribute.getOperator() == null) {

                    attribute.setOperator(ConjunctiveOperator.AND);
                }

                if((j == condition.getAttributes().length - 1) && (attribute.getOperator() == ConjunctiveOperator.AND || attribute.getOperator() == ConjunctiveOperator.OR)) {

                    attribute.setOperator(ConjunctiveOperator.NONE);
                }

                if((attribute.getOperator() == null || attribute.getOperator() == ConjunctiveOperator.NONE) && (attribute.getPredicates() == null || attribute.getPredicates().length == 0)) {

                    attribute.setPredicates(new EntityKeyPredicateInfo.Builder().setParent(attribute).build());
                }

                for(int k = 0; k < attribute.getPredicates().length; k++) {

                    EntityKeyPredicateInfo predicate = attribute.getPredicates()[k];

                    if(predicate.getKeys() == null || predicate.getKeys().length == 0) {

                        predicate.setKeys(new EntityKeyInfo(predicate, attribute.getType(), attribute.getAttribute()));
                    }

                    for(EntityKeyInfo key : predicate.getKeys()) {

                        key.setType(attribute.getType());
                    }

                    if(predicate.getComparison() == ComparisonOperator.NONE) {

                        predicate.setKeys();
                    }

                    if(predicate.getComparison() == null) {

                        predicate.setComparison(ComparisonOperator.IS);
                    }

                    if(predicate.getOperator() == null) {

                        predicate.setOperator(ConjunctiveOperator.AND);
                    }

                    if((k == attribute.getPredicates().length - 1) && (predicate.getOperator() == ConjunctiveOperator.AND || predicate.getOperator() == ConjunctiveOperator.OR)) {

                        predicate.setOperator(ConjunctiveOperator.NONE);
                    }

                    for(EntityKeyInfo key : predicate.getKeys()) {

                        key.setReferences(MethodContextHolder.getContext().getCompiler().getReferences(key));
                        key.setType(attribute.getType());

                        if(predicate.getComparison() == ComparisonOperator.IS_IN || predicate.getComparison() == ComparisonOperator.IS_NOT_IN) {

                            if(JPAUtils.getAttribute(manager, info.getType(), attribute.getAttribute()) instanceof AbstractPluralAttribute<?,?,?> field) {

                                SimpleTypeDescriptor<?> descriptor = field.getElementType();

                                if(descriptor.getPersistenceType() == Type.PersistenceType.ENTITY) {

                                    key.setType(JPAUtils.getIdType(manager, descriptor.getJavaType()).getJavaType());
                                    key.getAttributeInfo().setAttribute(new StringBuilder(key.getAttributeInfo().getAttribute()).append(".").append(JPAUtils.getId(manager, descriptor.getJavaType())).toString());

                                } else {

                                    key.setType(field.getElementType().getJavaType());
                                }

                                key.setType(TypeToken.getParameterized(attribute.getType(), key.getType()).getType());
                            }
                        }
                    }
                }
            }

            if(condition.getOperator() == null) {

                condition.setOperator(ConjunctiveOperator.AND);
            }

            if((i == info.getConditions().length - 1) && (condition.getOperator() == ConjunctiveOperator.AND || condition.getOperator() == ConjunctiveOperator.OR)) {

                condition.setOperator(ConjunctiveOperator.NONE);
            }
        }

        if(info.getInfo() == null) {

            info.setInfo(new EntityCollectionInfo(new EntityOrderInfo[0], new EntityPaginationInfo(0, 20,""), false, -1));
        }

        if(info.getInfo().getOrders() == null) {

            info.getInfo().setOrders(new EntityOrderInfo[0]);
        }

        if(info.getInfo().getPagination() == null) {

            info.getInfo().setPagination(new EntityPaginationInfo(0, 20, ""));
        }

        if(info.getInfo().getPagination().getReferences() == null) {

            info.getInfo().getPagination().setReferences(new String[0]);
        }

        if(info.getHints() == null) {

            info.setHints(new EntityQueryHintInfo[0]);
        }

        return info;
    }

    protected Set<String> getReferences(SpelNode node) {

        Set<String> result = new HashSet<>();

        int count = node.getChildCount();

        for(int i = 0; i < count; i++) {

            SpelNode child = node.getChild(i);

            if(child.getChildCount() > 0) {

                result.addAll(getReferences(child));

                continue;
            }

            String ast = child.toStringAST();

            if(ast.startsWith("#")) {

                result.add(ast.substring(1));
            }
        }

        return result;
    }

    static Class<?> getEntityType(MethodParameter parameter) {

        if(Iterable.class.isAssignableFrom(parameter.getParameterType()) || Optional.class.isAssignableFrom(parameter.getParameterType())) {

            return JPAUtils.getEntityType(parameter.getNestedGenericParameterType());
        }

        return JPAUtils.getEntityType(parameter.getParameterType());
    }

    static boolean isCollection(Class<?> type) {

        return type.isArray() || Iterable.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
    }
}
