package com.cobnet.spring.boot.controller.support;

import com.cobnet.common.Delegate;
import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springdoc.core.*;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.webmvc.core.RequestService;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springdoc.core.Constants.DOT;
import static org.springdoc.core.Constants.QUERY_PARAM;

public class CustomOpenApiRequestService extends RequestService {

    private GenericParameterService parameterBuilder;

    private RequestBodyService requestBodyService;

    private OperationService operationService;

    private Optional<List<ParameterCustomizer>> parameterCustomizers;

    private LocalVariableTableParameterNameDiscoverer localSpringDocParameterNameDiscoverer;

    /**
     * Instantiates a new Request builder.
     *
     * @param parameterBuilder                      the parameter builder
     * @param requestBodyService                    the request body builder
     * @param operationService                      the operation builder
     * @param parameterCustomizers                  the parameter customizers
     * @param localSpringDocParameterNameDiscoverer the local spring doc parameter name discoverer
     */
    public CustomOpenApiRequestService(GenericParameterService parameterBuilder, RequestBodyService requestBodyService, OperationService operationService, Optional<List<ParameterCustomizer>> parameterCustomizers, LocalVariableTableParameterNameDiscoverer localSpringDocParameterNameDiscoverer) {
        super(parameterBuilder, requestBodyService, operationService, parameterCustomizers, localSpringDocParameterNameDiscoverer);
        this.parameterBuilder = parameterBuilder;
        this.requestBodyService = requestBodyService;
        this.operationService = operationService;
        this.parameterCustomizers = parameterCustomizers;
        this.localSpringDocParameterNameDiscoverer = localSpringDocParameterNameDiscoverer;
    }


    @Override
    public Operation build(HandlerMethod handlerMethod, RequestMethod requestMethod, Operation operation, MethodAttributes methodAttributes, OpenAPI openAPI) {

        String operationId = operationService.getOperationId(handlerMethod.getMethod().getName(),
                operation.getOperationId(), openAPI);
        operation.setOperationId(operationId);
        // requests
        String[] pNames = this.localSpringDocParameterNameDiscoverer.getParameterNames(handlerMethod.getMethod());
        MethodParameter[] parameters = handlerMethod.getMethodParameters();
        String[] reflectionParametersNames = Arrays.stream(handlerMethod.getMethod().getParameters()).map(java.lang.reflect.Parameter::getName).toArray(String[]::new);
        if (pNames == null || Arrays.stream(pNames).anyMatch(Objects::isNull))
            pNames = reflectionParametersNames;
        parameters = DelegatingMethodParameter.customize(pNames, parameters, parameterBuilder.getDelegatingMethodParameterCustomizer());
        RequestBodyInfo requestBodyInfo = new RequestBodyInfo();
        List<Parameter> operationParameters = (operation.getParameters() != null) ? operation.getParameters() : new ArrayList<>();
        Map<String, io.swagger.v3.oas.annotations.Parameter> parametersDocMap = getApiParameters(handlerMethod.getMethod());
        Components components = openAPI.getComponents();

        JavadocProvider javadocProvider = operationService.getJavadocProvider();

        for (MethodParameter methodParameter : parameters) {
            // check if query param
            Parameter parameter;
            io.swagger.v3.oas.annotations.Parameter parameterDoc = AnnotatedElementUtils.findMergedAnnotation(
                    AnnotatedElementUtils.forAnnotations(methodParameter.getParameterAnnotations()),
                    io.swagger.v3.oas.annotations.Parameter.class);

            final String pName = methodParameter.getParameterName();
            ParameterInfo parameterInfo = new ParameterInfo(pName, methodParameter, parameterBuilder);

            if (parameterDoc == null)
                parameterDoc = parametersDocMap.get(parameterInfo.getpName());
            // use documentation as reference
            if (parameterDoc != null) {
                if (parameterDoc.hidden() || parameterDoc.schema().hidden())
                    continue;

                parameter = parameterBuilder.buildParameterFromDoc(parameterDoc, components, methodAttributes.getJsonViewAnnotation(), methodAttributes.getLocale());
                parameterInfo.setParameterModel(parameter);
            }

            if (!isParamToIgnore(methodParameter)) {
                parameter = buildParams(parameterInfo, components, requestMethod, methodAttributes.getJsonViewAnnotation());
                // Merge with the operation parameters
                parameter = GenericParameterService.mergeParameter(operationParameters, parameter);

                List<Annotation> parameterAnnotations = Arrays.asList(methodParameter.getParameterAnnotations());

                if (isValidParameter(parameter)) {
                    // Add param javadoc
                    if (StringUtils.isBlank(parameter.getDescription()) && javadocProvider != null) {
                        String paramJavadocDescription = getParamJavadoc(javadocProvider, methodParameter, pName);
                        if (!StringUtils.isBlank(paramJavadocDescription)) {
                            parameter.setDescription(paramJavadocDescription);
                        }
                    }
                    applyBeanValidatorAnnotations(parameter, parameterAnnotations);
                }
                else if (!RequestMethod.GET.equals(requestMethod)) {

                    if (operation.getRequestBody() != null) {
                        requestBodyInfo.setRequestBody(operation.getRequestBody());
                    }

                    if(ApplicationJson.class.isAssignableFrom(methodParameter.getParameterType())) {
                        requestBodyInfo.setRequestBody(new Delegate<>(new io.swagger.v3.oas.models.parameters.RequestBody()).call(delegator -> delegator.setRequired(true)));
                    }

                    requestBodyService.calculateRequestBodyInfo(components, methodAttributes,
                            parameterInfo, requestBodyInfo);
                    // Add requestBody javadoc
                    if (StringUtils.isBlank(requestBodyInfo.getRequestBody().getDescription()) && javadocProvider != null) {
                        String paramJavadocDescription = getParamJavadoc(javadocProvider, methodParameter, pName);

                        if (!StringUtils.isBlank(paramJavadocDescription)) {
                            requestBodyInfo.getRequestBody().setDescription(paramJavadocDescription);
                        }
                    }
                    applyBeanValidatorAnnotations(requestBodyInfo.getRequestBody(), parameterAnnotations, methodParameter.isOptional());
                }
                customiseParameter(parameter, parameterInfo, operationParameters);
            }
        }

        LinkedHashMap<String, Parameter> map = getParameterLinkedHashMap(components, methodAttributes, operationParameters, parametersDocMap);
        setParams(operation, new ArrayList<>(map.values()), requestBodyInfo);
        return operation;
    }

    @Override
    public Parameter buildParams(ParameterInfo parameterInfo, Components components,
                                 RequestMethod requestMethod, JsonView jsonView) {
        MethodParameter methodParameter = parameterInfo.getMethodParameter();
        if (parameterInfo.getParamType() != null) {
            if (!ValueConstants.DEFAULT_NONE.equals(parameterInfo.getDefaultValue()))
                parameterInfo.setRequired(false);
            else
                parameterInfo.setDefaultValue(null);
            return this.buildParam(parameterInfo, components, jsonView);
        }
        // By default
        if (!isRequestBodyParam(requestMethod, parameterInfo)) {
            parameterInfo.setRequired(!((DelegatingMethodParameter) methodParameter).isNotRequired() && !methodParameter.isOptional());
            parameterInfo.setParamType(QUERY_PARAM);
            parameterInfo.setDefaultValue(null);
            return this.buildParam(parameterInfo, components, jsonView);
        }
        return null;
    }

    private boolean isRequestBodyParam(RequestMethod requestMethod, ParameterInfo parameterInfo) {
        MethodParameter methodParameter = parameterInfo.getMethodParameter();
        DelegatingMethodParameter delegatingMethodParameter = (DelegatingMethodParameter) methodParameter;

        return (!RequestMethod.GET.equals(requestMethod) && (parameterInfo.getParameterModel() == null || parameterInfo.getParameterModel().getIn() == null) && !delegatingMethodParameter.isParameterObject())
                &&
                ((ApplicationJson.class.isAssignableFrom(methodParameter.getParameterType())
                        || methodParameter.getParameterAnnotation(io.swagger.v3.oas.annotations.parameters.RequestBody.class) != null
                        || methodParameter.getParameterAnnotation(org.springframework.web.bind.annotation.RequestBody.class) != null
                        || methodParameter.getParameterAnnotation(org.springframework.web.bind.annotation.RequestPart.class) != null
                        || AnnotatedElementUtils.findMergedAnnotation(Objects.requireNonNull(methodParameter.getMethod()), io.swagger.v3.oas.annotations.parameters.RequestBody.class) != null)
                        || (!ClassUtils.isPrimitiveOrWrapper(methodParameter.getParameterType()) && (!ArrayUtils.isEmpty(methodParameter.getParameterAnnotations()))));
    }

    private String getParamJavadoc(JavadocProvider javadocProvider, MethodParameter methodParameter, String pName) {
        DelegatingMethodParameter delegatingMethodParameter = (DelegatingMethodParameter) methodParameter;
        final String paramJavadocDescription;
        if (delegatingMethodParameter.isParameterObject()) {
            String fieldName;
            if (StringUtils.isNotEmpty(pName) && pName.contains(DOT))
                fieldName = StringUtils.substringAfter(pName, DOT);
            else
                fieldName = pName;
            Field field = FieldUtils.getDeclaredField(((DelegatingMethodParameter) methodParameter).getExecutable().getDeclaringClass(), fieldName, true);
            paramJavadocDescription = javadocProvider.getFieldJavadoc(field);
        }
        else
            paramJavadocDescription = javadocProvider.getParamJavadoc(methodParameter.getMethod(), pName);
        return paramJavadocDescription;
    }

    private LinkedHashMap<String, Parameter> getParameterLinkedHashMap(Components components, MethodAttributes methodAttributes, List<Parameter> operationParameters, Map<String, io.swagger.v3.oas.annotations.Parameter> parametersDocMap) {
        LinkedHashMap<String, Parameter> map = operationParameters.stream()
                .collect(Collectors.toMap(
                        parameter -> parameter.getName() != null ? parameter.getName() : Integer.toString(parameter.hashCode()),
                        parameter -> parameter,
                        (u, v) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                        },
                        LinkedHashMap::new
                ));

        for (Map.Entry<String, io.swagger.v3.oas.annotations.Parameter> entry : parametersDocMap.entrySet()) {
            if (entry.getKey() != null && !map.containsKey(entry.getKey()) && !entry.getValue().hidden()) {
                //Convert
                Parameter parameter = parameterBuilder.buildParameterFromDoc(entry.getValue(), components,
                        methodAttributes.getJsonViewAnnotation(), methodAttributes.getLocale());
                map.put(entry.getKey(), parameter);
            }
        }

        getHeaders(methodAttributes, map);
        return map;
    }

    private void setParams(Operation operation, List<Parameter> operationParameters, RequestBodyInfo requestBodyInfo) {
        if (!CollectionUtils.isEmpty(operationParameters))
            operation.setParameters(operationParameters);
        if (requestBodyInfo.getRequestBody() != null)
            operation.setRequestBody(requestBodyInfo.getRequestBody());
    }


    private Map<String, io.swagger.v3.oas.annotations.Parameter> getApiParameters(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();

        Set<io.swagger.v3.oas.annotations.Parameters> apiParametersDoc = AnnotatedElementUtils
                .findAllMergedAnnotations(method, io.swagger.v3.oas.annotations.Parameters.class);
        LinkedHashMap<String, io.swagger.v3.oas.annotations.Parameter> apiParametersMap = apiParametersDoc.stream()
                .flatMap(x -> Stream.of(x.value())).collect(Collectors.toMap(io.swagger.v3.oas.annotations.Parameter::name, x -> x, (e1, e2) -> e2,
                        LinkedHashMap::new));

        Set<io.swagger.v3.oas.annotations.Parameters> apiParametersDocDeclaringClass = AnnotatedElementUtils
                .findAllMergedAnnotations(declaringClass, io.swagger.v3.oas.annotations.Parameters.class);
        LinkedHashMap<String, io.swagger.v3.oas.annotations.Parameter> apiParametersDocDeclaringClassMap = apiParametersDocDeclaringClass.stream()
                .flatMap(x -> Stream.of(x.value())).collect(Collectors.toMap(io.swagger.v3.oas.annotations.Parameter::name, x -> x, (e1, e2) -> e2,
                        LinkedHashMap::new));
        apiParametersMap.putAll(apiParametersDocDeclaringClassMap);

        Set<io.swagger.v3.oas.annotations.Parameter> apiParameterDoc = AnnotatedElementUtils
                .findAllMergedAnnotations(method, io.swagger.v3.oas.annotations.Parameter.class);
        LinkedHashMap<String, io.swagger.v3.oas.annotations.Parameter> apiParameterDocMap = apiParameterDoc.stream()
                .collect(Collectors.toMap(io.swagger.v3.oas.annotations.Parameter::name, x -> x, (e1, e2) -> e2,
                        LinkedHashMap::new));
        apiParametersMap.putAll(apiParameterDocMap);

        Set<io.swagger.v3.oas.annotations.Parameter> apiParameterDocDeclaringClass = AnnotatedElementUtils
                .findAllMergedAnnotations(declaringClass, io.swagger.v3.oas.annotations.Parameter.class);
        LinkedHashMap<String, io.swagger.v3.oas.annotations.Parameter> apiParameterDocDeclaringClassMap = apiParameterDocDeclaringClass.stream()
                .collect(Collectors.toMap(io.swagger.v3.oas.annotations.Parameter::name, x -> x, (e1, e2) -> e2,
                        LinkedHashMap::new));
        apiParametersMap.putAll(apiParameterDocDeclaringClassMap);

        return apiParametersMap;
    }
}
