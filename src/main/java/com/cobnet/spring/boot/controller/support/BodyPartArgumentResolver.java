package com.cobnet.spring.boot.controller.support;

import com.cobnet.common.JPAUtils;
import com.cobnet.interfaces.spring.controller.BodyPart;
import com.cobnet.interfaces.spring.controller.BodyPartKey;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.dynalink.linker.support.TypeUtilities;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class BodyPartArgumentResolver extends RequestResponseBodyMethodProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(BodyPartArgumentResolver.class);

    private final ObjectMapper mapper;

    private final EntityManager entityManager;

    private final CacheManager cacheManager;


    public BodyPartArgumentResolver(ObjectMapper mapper, EntityManager entityManager, CacheManager cacheManager, List<HttpMessageConverter<?>> converters) {
        super(converters);
        this.mapper = mapper;
        this.entityManager = entityManager;
        this.cacheManager = cacheManager;
    }

    public BodyPartArgumentResolver(ObjectMapper mapper, EntityManager entityManager, CacheManager cacheManager, List<HttpMessageConverter<?>> converters, ContentNegotiationManager manager) {
        super(converters, manager);
        this.mapper = mapper;
        this.entityManager = entityManager;
        this.cacheManager = cacheManager;
    }

    public BodyPartArgumentResolver(ObjectMapper mapper, EntityManager entityManager, CacheManager cacheManager, List<HttpMessageConverter<?>> converters, List<Object> requestResponseBodyAdvice) {
        super(converters, requestResponseBodyAdvice);
        this.mapper = mapper;
        this.entityManager = entityManager;
        this.cacheManager = cacheManager;
    }

    public BodyPartArgumentResolver(ObjectMapper mapper, EntityManager entityManager, CacheManager cacheManager, List<HttpMessageConverter<?>> converters, ContentNegotiationManager manager, List<Object> requestResponseBodyAdvice) {
        super(converters, manager, requestResponseBodyAdvice);
        this.mapper = mapper;
        this.entityManager = entityManager;
        this.cacheManager = cacheManager;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        return parameter instanceof ReferencableMethodParameter;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container, NativeWebRequest request, WebDataBinderFactory factory) throws Exception {

        Object result = null;

        parameter = parameter.nestedIfOptional();

        BodyPartExpressionCompiler compiler = MethodContextHolder.getContext().getCompiler();

        Object data = compiler.getData() == null ? super.readWithMessageConverters(request, parameter, parameter.getNestedGenericParameterType()) : compiler.getData();

        BodyPart info = MethodContextHolder.getContext().getInfo(parameter);

        if(data instanceof JsonNode node) {

            //info = resolveData(compiler,info, node);
            info = resolveDataA(info, compiler);
        }

        if(info instanceof EntityVariableInfo<?> variable) {

            TypedQuery<?> query = resolveQuery(variable);

            if(TypeUtilities.isMethodInvocationConvertible(parameter.getParameterType(), Iterable.class)) {

                if(variable.getInfo().getTotal() > -1) {

                    query.setMaxResults((int) variable.getInfo().getTotal());
                }

                if(TypeUtilities.isMethodInvocationConvertible(parameter.getParameterType(), Slice.class)) {

                    Pageable pageable = variable.getInfo().getPagination().getValue();

                    query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
                    query.setMaxResults(pageable.getPageSize());

                    result = new PageImpl<>(query.getResultList(), pageable, pageable.getPageSize());
                }

                if(TypeUtilities.isMethodInvocationConvertible(parameter.getParameterType(), Set.class)) {

                    result = new HashSet<>(query.getResultList());
                }

                if(TypeUtilities.isMethodInvocationConvertible(parameter.getParameterType(), List.class)) {

                    result = new ArrayList<>(query.getResultList());
                }

            } else {

                query.setMaxResults(1);

                if(TypeUtilities.isMethodInvocationConvertible(parameter.getParameterType(), Optional.class)) {

                    result = query.getResultList().stream().findFirst();

                } else {

                    result = query.getResultList().stream().findFirst().orElse(null);
                }
            }
            System.out.println(JPAUtils.getQueryString(entityManager, query));
            if(LOG.isDebugEnabled()) {

                LOG.debug(JPAUtils.getQueryString(entityManager, query));
            }
        }

        if (factory != null) {

            WebDataBinder binder = factory.createBinder(request, data, info.getName());

            if (data != null) {

                this.validateIfApplicable(binder, parameter);

                if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {

                    throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
                }
            }

            if (container != null) {

                container.addAttribute(new StringBuilder(BindingResult.MODEL_KEY_PREFIX).append(info.getName()).toString(), binder.getBindingResult());
            }
        }

        MethodContextHolder.getContext().getCompiler().addArgument(info.getName(), result);

        return result;
    }

    protected <T> TypedQuery<T> resolveQuery(EntityVariableInfo<T> variable) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<T> criteria = builder.createQuery(variable.getType());

        variable.generate(entityManager, builder, criteria);

        TypedQuery<T> query = entityManager.createQuery(criteria);

        query.setFlushMode(variable.getFlush());

        return query;
    }

    protected BodyPart resolveDataA(BodyPart part, BodyPartExpressionCompiler compiler) throws IOException {

        for(BodyPartKey key : part.getKeys()) {

            var value = compiler.compile(key);

            System.out.println(value);
            //key.setValue(compiler.compile(key.getKey(), key.getType()));
        }

        return part;
    }

    protected BodyPart resolveData(BodyPartExpressionCompiler compiler, BodyPart part, JsonNode node) throws IllegalAccessException, IOException {

        if(part.isIsolated()) {

            node = node.get(part.getName());
        }

        if(part.isRequired() && node == null) {

            throw new MissingFormatArgumentException(String.format("Unable to find required section '%s' in request json data.", part.getName()));
        }

        if(part instanceof EntityVariableInfo<?> info) {

            Pageable pageable = PageRequest.of(info.getInfo().getPagination().getNumber(), info.getInfo().getPagination().getSize());

            if(info.getInfo().getPagination().getExpression() != null && info.getInfo().getPagination().getExpression().length() > 0) {

                for(String reference : info.getInfo().getPagination().getReferences()) {

                    Object referenced = compiler.getArgs().get(reference);

                    if(referenced == null) {

                        throw new MissingFormatArgumentException(String.format("Referenced '%s' instance is null.", reference));
                    }

                    compiler.getContext().setVariable(reference, compiler.getArgs().get(reference));
                }

                Object value = compiler.getParser().parseExpression(info.getInfo().getPagination().getExpression()).getValue(compiler.getContext());

                if(!(value instanceof Pageable)) {

                    throw new IllegalArgumentException(String.format("Illegal return type of %s, expression( | %s | ) return value must be %s.", value.getClass().getName(), info.getInfo().getPagination().getExpression(), Pageable.class.getName()));
                }

                pageable = (Pageable) value;
            }

            info.getInfo().getPagination().setValue(pageable);
        }

        for(BodyPartKey key : part.getKeys()) {

            JsonNode field = node.get(key.getKey());

            if(field == null) {

                if(key instanceof EntityKeyInfo info && !info.isNullable() && info.getReferences() == null) {

                    throw new MissingFormatArgumentException(String.format("Unable to find required key '%s' for parameter '%s' in data.", key.getKey(), part.getName()));
                }
            }

            if(key instanceof EntityKeyInfo info) {

                key.setValue(info.getAttributeInfo().getKeyValue(key.getKey()));

                if(key.getValue() != null) {

                    continue;
                }

                if(info.getReferences() != null && info.getReferences().length > 0) {

                    for(String reference : info.getReferences()) {

                        Object referenced = compiler.getArgs().get(reference);

                        if(referenced == null) {

                            throw new MissingFormatArgumentException(String.format("Referenced '%s' instance is null.", reference));
                        }

                        compiler.getContext().setVariable(reference, compiler.getArgs().get(reference));
                    }

                    key.setValue(compiler.getParser().parseExpression(key.getKey()).getValue(compiler.getContext()));

                    continue;
                }
            }

            if(BodyPartArgumentResolver.isCollection(key.getType())) {

                if(!field.isArray()) {

                    throw new MissingFormatArgumentException(String.format("Unable format key '%s', because data is not an array.", key.getKey()));
                }

                List<Object> elements = new ArrayList<>();

                Iterator<JsonNode> it = field.elements();

                while(it.hasNext()) {

                    JsonNode element = it.next();

                    elements.add(mapper.readValue(element.traverse(), BodyPartArgumentResolver.getType(key.getType())));
                }

                key.setValue(elements);

                continue;
            }

            if(field != null) {

                key.setValue(mapper.readValue(field.traverse(), BodyPartArgumentResolver.getType(key.getType())));
            }
        }

        return part;
    }

    static Object setPropertyValue(EntityManager manager, EntityTuplizer tuplizer, Object instance, String name, Object value) {

        if(instance == null) {

            return null;
        }

        if(JPAUtils.isJPAEntity(instance.getClass())) {

            return JPAUtils.setProperty(manager, tuplizer, instance, name, value);
        }

        ReflectionUtils.setField(ReflectionUtils.findField(instance.getClass(), name), instance, value);

        return instance;
    }

    static <T> T getPropertyValue(EntityManager manager, Object instance, String property) throws IllegalAccessException {

        if(instance == null) {

            return null;
        }

        if(JPAUtils.isJPAEntity(instance.getClass())) {

            return (T) JPAUtils.getProperty(manager, instance, property);
        }

        return (T) ReflectionUtils.findField(instance.getClass(), property).get(instance);
    }

    static Class<?> getType(Type type) {

        if(type instanceof Class<?> clazz) {

            return clazz;
        }

        if(type instanceof ParameterizedType parameterized) {

            return BodyPartArgumentResolver.getType(parameterized.getRawType());
        }

        return null;
    }

    static boolean isCollection(Type type) {

        if(type instanceof Class<?> clazz) {

            return clazz.isArray() || TypeUtilities.isMethodInvocationConvertible(clazz, Iterable.class);
        }

        if(type instanceof ParameterizedType parameterized) {

            return BodyPartArgumentResolver.isCollection(parameterized.getRawType());
        }

        return false;
    }
}
