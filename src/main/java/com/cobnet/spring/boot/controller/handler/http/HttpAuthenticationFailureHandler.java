package com.cobnet.spring.boot.controller.handler.http;

import com.cobnet.spring.boot.configuration.SecurityConfiguration;
import com.cobnet.spring.boot.core.HttpRequestUrlResolver;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.AuthenticationResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.session.Session;
import org.springframework.stereotype.Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class HttpAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String url = HttpRequestUrlResolver.getFailureRedirectUrl(request);

        if(url == null) {

            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ProjectBeanHolder.getObjectMapper().writeValueAsString(new AuthenticationResult(false, null)));

        } else {

            ProjectBeanHolder.getRedirectStrategy().sendRedirect(request, response, url);
        }



    }
}
