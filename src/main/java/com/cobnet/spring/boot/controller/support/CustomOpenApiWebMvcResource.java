package com.cobnet.spring.boot.controller.support;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.core.util.ReflectionUtils;
import io.swagger.v3.oas.annotations.callbacks.Callback;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.*;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.filters.OpenApiMethodFilter;
import org.springdoc.core.fn.RouterOperation;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static org.springdoc.core.converters.SchemaPropertyDeprecatingConverter.isDeprecated;

@Primary
@RestController
public class CustomOpenApiWebMvcResource extends OpenApiWebMvcResource {

    private ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory;
    private AbstractRequestService requestBuilder;
    private GenericResponseService responseBuilder;
    private OperationService operationParser;
    private Optional<List<OperationCustomizer>> operationCustomizers;
    private Optional<List<OpenApiCustomiser>> openApiCustomisers;
    private Optional<List<OpenApiMethodFilter>> methodFilters;
    private SpringDocConfigProperties springDocConfigProperties;
    private SpringDocProviders springDocProvider;


    public CustomOpenApiWebMvcResource(String groupName, ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory, AbstractRequestService requestBuilder, GenericResponseService responseBuilder, OperationService operationParser, Optional<List<OperationCustomizer>> operationCustomizers, Optional<List<OpenApiCustomiser>> openApiCustomisers, Optional<List<OpenApiMethodFilter>> methodFilters, SpringDocConfigProperties springDocConfigProperties, SpringDocProviders springDocProviders) {
        super(groupName, openAPIBuilderObjectFactory, requestBuilder, responseBuilder, operationParser, operationCustomizers, openApiCustomisers, methodFilters, springDocConfigProperties, springDocProviders);
    }

    @Autowired
    public CustomOpenApiWebMvcResource(ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory, AbstractRequestService requestBuilder, GenericResponseService responseBuilder, OperationService operationParser, Optional<List<OperationCustomizer>> operationCustomizers, Optional<List<OpenApiCustomiser>> openApiCustomisers, Optional<List<OpenApiMethodFilter>> methodFilters, SpringDocConfigProperties springDocConfigProperties, SpringDocProviders springDocProviders) {
        super(openAPIBuilderObjectFactory, requestBuilder, responseBuilder, operationParser, operationCustomizers, openApiCustomisers, methodFilters, springDocConfigProperties, springDocProviders);
        this.openAPIBuilderObjectFactory = openAPIBuilderObjectFactory;
        this.requestBuilder = requestBuilder;
        this.responseBuilder = responseBuilder;
        this.operationParser = operationParser;
        this.operationCustomizers = operationCustomizers;
        this.openApiCustomisers = openApiCustomisers;
        this.methodFilters = methodFilters;
        this.springDocConfigProperties = springDocConfigProperties;
        this.springDocProvider = springDocProviders;
    }

    @Override
    protected void calculatePath(HandlerMethod handlerMethod, RouterOperation routerOperation, Locale locale) {
        String operationPath = routerOperation.getPath();
        Set<RequestMethod> requestMethods = new HashSet<>(Arrays.asList(routerOperation.getMethods()));
        io.swagger.v3.oas.annotations.Operation apiOperation = routerOperation.getOperation();
        String[] methodConsumes = routerOperation.getConsumes();
        String[] methodProduces = routerOperation.getProduces();
        String[] headers = routerOperation.getHeaders();
        Map<String, String> queryParams = routerOperation.getQueryParams();

        OpenAPI openAPI = openAPIService.getCalculatedOpenAPI();
        Components components = openAPI.getComponents();
        Paths paths = openAPI.getPaths();

        Map<PathItem.HttpMethod, Operation> operationMap = null;
        if (paths.containsKey(operationPath)) {
            PathItem pathItem = paths.get(operationPath);
            operationMap = pathItem.readOperationsMap();
        }

        JavadocProvider javadocProvider = operationParser.getJavadocProvider();

        for (RequestMethod requestMethod : requestMethods) {
            Operation existingOperation = getExistingOperation(operationMap, requestMethod);
            Method method = handlerMethod.getMethod();
            // skip hidden operations
            if (operationParser.isHidden(method))
                continue;

            RequestMapping reqMappingClass = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(),
                    RequestMapping.class);

            MethodAttributes methodAttributes = new MethodAttributes(springDocConfigProperties.getDefaultConsumesMediaType(), springDocConfigProperties.getDefaultProducesMediaType(), methodConsumes, methodProduces, headers, locale);

            methodAttributes.setMethodOverloaded(existingOperation != null);
            //Use the javadoc return if present
            if (javadocProvider != null) {
                methodAttributes.setJavadocReturn(javadocProvider.getMethodJavadocReturn(handlerMethod.getMethod()));
            }

            if (reqMappingClass != null) {
                methodAttributes.setClassConsumes(reqMappingClass.consumes());
                methodAttributes.setClassProduces(reqMappingClass.produces());
            }

            methodAttributes.calculateHeadersForClass(method.getDeclaringClass());
            methodAttributes.calculateConsumesProduces(method);

            Operation operation = (existingOperation != null) ? existingOperation : new Operation();
            if (isDeprecated(method))
                operation.setDeprecated(true);

            // Add documentation from operation annotation
            if (apiOperation == null || StringUtils.isBlank(apiOperation.operationId()))
                apiOperation = AnnotatedElementUtils.findMergedAnnotation(method,
                        io.swagger.v3.oas.annotations.Operation.class);

            calculateJsonView(apiOperation, methodAttributes, method);
            if (apiOperation != null)
                openAPI = operationParser.parse(apiOperation, operation, openAPI, methodAttributes);
            fillParametersList(operation, queryParams, methodAttributes);

            // compute tags
            operation = openAPIService.buildTags(handlerMethod, operation, openAPI, locale);

            io.swagger.v3.oas.annotations.parameters.RequestBody requestBodyDoc = AnnotatedElementUtils.findMergedAnnotation(method,
                    io.swagger.v3.oas.annotations.parameters.RequestBody.class);

            // RequestBody in Operation
            requestBuilder.getRequestBodyBuilder()
                    .buildRequestBodyFromDoc(requestBodyDoc, methodAttributes, components,
                            methodAttributes.getJsonViewAnnotationForRequestBody())
                    .ifPresent(operation::setRequestBody);
            // requests
            operation = requestBuilder.build(handlerMethod, requestMethod, operation, methodAttributes, openAPI);

            // responses
            ApiResponses apiResponses = responseBuilder.build(components, handlerMethod, operation, methodAttributes);
            operation.setResponses(apiResponses);

            // get javadoc method description
            if (javadocProvider != null) {
                String description = javadocProvider.getMethodJavadocDescription(handlerMethod.getMethod());
                if (!StringUtils.isEmpty(description) && StringUtils.isEmpty(operation.getDescription()))
                    operation.setDescription(description);
            }

            Set<io.swagger.v3.oas.annotations.callbacks.Callback> apiCallbacks = AnnotatedElementUtils.findMergedRepeatableAnnotations(method, io.swagger.v3.oas.annotations.callbacks.Callback.class);

            // callbacks
            buildCallbacks(openAPI, methodAttributes, operation, apiCallbacks);

            // allow for customisation
            operation = customiseOperation(operation, handlerMethod);

            PathItem pathItemObject = buildPathItem(requestMethod, operation, operationPath, paths);
            paths.addPathItem(operationPath, pathItemObject);
        }
    }

    private void calculateJsonView(io.swagger.v3.oas.annotations.Operation apiOperation,
                                   MethodAttributes methodAttributes, Method method) {
        JsonView jsonViewAnnotation;
        JsonView jsonViewAnnotationForRequestBody;
        if (apiOperation != null && apiOperation.ignoreJsonView()) {
            jsonViewAnnotation = null;
            jsonViewAnnotationForRequestBody = null;
        }
        else {
            jsonViewAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, JsonView.class);
            /*
             * If one and only one exists, use the @JsonView annotation from the method
             * parameter annotated with @RequestBody. Otherwise fall back to the @JsonView
             * annotation for the method itself.
             */
            jsonViewAnnotationForRequestBody = (JsonView) Arrays.stream(ReflectionUtils.getParameterAnnotations(method))
                    .filter(arr -> Arrays.stream(arr)
                            .anyMatch(annotation -> (annotation.annotationType()
                                    .equals(io.swagger.v3.oas.annotations.parameters.RequestBody.class) || annotation.annotationType().equals(RequestBody.class))))
                    .flatMap(Arrays::stream).filter(annotation -> annotation.annotationType().equals(JsonView.class))
                    .reduce((a, b) -> null).orElse(jsonViewAnnotation);
        }
        methodAttributes.setJsonViewAnnotation(jsonViewAnnotation);
        methodAttributes.setJsonViewAnnotationForRequestBody(jsonViewAnnotationForRequestBody);
    }

    private void fillParametersList(Operation operation, Map<String, String> queryParams, MethodAttributes methodAttributes) {
        List<Parameter> parametersList = operation.getParameters();
        if (parametersList == null)
            parametersList = new ArrayList<>();
        Collection<Parameter> headersMap = AbstractRequestService.getHeaders(methodAttributes, new LinkedHashMap<>());
        parametersList.addAll(headersMap);
        if (!CollectionUtils.isEmpty(queryParams)) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                io.swagger.v3.oas.models.parameters.Parameter parameter = new io.swagger.v3.oas.models.parameters.Parameter();
                parameter.setName(entry.getKey());
                parameter.setSchema(new StringSchema()._default(entry.getValue()));
                parameter.setRequired(true);
                parameter.setIn(ParameterIn.QUERY.toString());
                GenericParameterService.mergeParameter(parametersList, parameter);
            }
            operation.setParameters(parametersList);
        }
    }

    private PathItem buildPathItem(RequestMethod requestMethod, Operation operation, String operationPath,
                                   Paths paths) {
        PathItem pathItemObject;
        if (paths.containsKey(operationPath))
            pathItemObject = paths.get(operationPath);
        else
            pathItemObject = new PathItem();

        switch (requestMethod) {
            case POST:
                pathItemObject.post(operation);
                break;
            case GET:
                pathItemObject.get(operation);
                break;
            case DELETE:
                pathItemObject.delete(operation);
                break;
            case PUT:
                pathItemObject.put(operation);
                break;
            case PATCH:
                pathItemObject.patch(operation);
                break;
            case TRACE:
                pathItemObject.trace(operation);
                break;
            case HEAD:
                pathItemObject.head(operation);
                break;
            case OPTIONS:
                pathItemObject.options(operation);
                break;
            default:
                // Do nothing here
                break;
        }
        return pathItemObject;
    }

    private void buildCallbacks(OpenAPI openAPI, MethodAttributes methodAttributes, Operation operation, Set<Callback> apiCallbacks) {
        if (!CollectionUtils.isEmpty(apiCallbacks))
            operationParser.buildCallbacks(apiCallbacks, openAPI, methodAttributes)
                    .ifPresent(operation::setCallbacks);
    }

    private Operation getExistingOperation(Map<PathItem.HttpMethod, Operation> operationMap, RequestMethod requestMethod) {
        Operation existingOperation = null;
        if (!CollectionUtils.isEmpty(operationMap)) {
            // Get existing operation definition
            switch (requestMethod) {
                case GET:
                    existingOperation = operationMap.get(PathItem.HttpMethod.GET);
                    break;
                case POST:
                    existingOperation = operationMap.get(PathItem.HttpMethod.POST);
                    break;
                case PUT:
                    existingOperation = operationMap.get(PathItem.HttpMethod.PUT);
                    break;
                case DELETE:
                    existingOperation = operationMap.get(PathItem.HttpMethod.DELETE);
                    break;
                case PATCH:
                    existingOperation = operationMap.get(PathItem.HttpMethod.PATCH);
                    break;
                case HEAD:
                    existingOperation = operationMap.get(PathItem.HttpMethod.HEAD);
                    break;
                case OPTIONS:
                    existingOperation = operationMap.get(PathItem.HttpMethod.OPTIONS);
                    break;
                default:
                    // Do nothing here
                    break;
            }
        }
        return existingOperation;
    }
}
