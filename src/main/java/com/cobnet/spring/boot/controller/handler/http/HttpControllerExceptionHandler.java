package com.cobnet.spring.boot.controller.handler.http;

import com.cobnet.exception.ResponseFailureStatusException;
import com.cobnet.interfaces.connection.web.Content;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.MessageWrapper;
import com.cobnet.spring.boot.dto.ObjectWrapper;
import com.cobnet.spring.boot.dto.ResponseResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class HttpControllerExceptionHandler {

    @ExceptionHandler(ResponseFailureStatusException.class)
    public void handleResponseFailureStatusException(HttpServletRequest request, HttpServletResponse response, ResponseFailureStatusException exception) {

        ProjectBeanHolder.getSecurityService().addBadMessage(request);

        try(Writer writer = response.getWriter()) {

            response.setStatus(exception.getStatus().getCode());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            String message = exception.getStatus().message();

            MessageWrapper errorMessage = null;

            if(message != null) {

                errorMessage = new MessageWrapper("error", message);
            }

            List<Content<?>> params = new ArrayList<>();

            if(errorMessage != null) {

                params.add(errorMessage);
            }

            for(Content<?> content : exception.getParams()) {

                params.add(content);
            }

            writer.write(ProjectBeanHolder.getObjectMapper().writeValueAsString(new ResponseResult<>(exception.getStatus(), params)));

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }
}
