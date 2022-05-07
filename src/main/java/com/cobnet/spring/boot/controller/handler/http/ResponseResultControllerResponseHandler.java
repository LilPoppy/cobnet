package com.cobnet.spring.boot.controller.handler.http;

import com.cobnet.interfaces.connection.web.ReasonableStatus;
import com.cobnet.spring.boot.dto.ResponseResult;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ResponseResultControllerResponseHandler implements ResponseBodyAdvice<ResponseResult<? extends ReasonableStatus>> {


    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {

        return ResponseResult.class.isAssignableFrom(returnType.getMethod().getReturnType());
    }

    @Override
    public ResponseResult<? extends ReasonableStatus> beforeBodyWrite(ResponseResult<? extends ReasonableStatus> body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        //TODO handle spam messages
        response.setStatusCode(body.status().getStatus());

        return body;
    }
}
