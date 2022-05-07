package com.cobnet.spring.boot.controller.handler.http;

import com.cobnet.exception.ResponseFailureStatusException;
import com.cobnet.interfaces.connection.web.ReasonableStatus;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.ResponseResult;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

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

    @ExceptionHandler(ResponseFailureStatusException.class)
    public void handleException(HttpServletResponse response, ResponseFailureStatusException exception) {

        try(Writer writer = response.getWriter()) {

            response.setStatus(exception.getStatus().getCode());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            writer.write(ProjectBeanHolder.getObjectMapper().writeValueAsString(new ResponseResult<>(exception.getStatus(), exception.getParams())));

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }
}
