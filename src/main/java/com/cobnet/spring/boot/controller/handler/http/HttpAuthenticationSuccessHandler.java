package com.cobnet.spring.boot.controller.handler.http;

import com.cobnet.security.AccountAuthenticationToken;
import com.cobnet.spring.boot.configuration.SecurityConfiguration;
import com.cobnet.spring.boot.core.HttpRequestUrlResolver;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.AuthenticationResult;
import com.cobnet.spring.boot.dto.ConnectionToken;
import com.cobnet.spring.boot.dto.RememberMeInfo;
import com.cobnet.spring.boot.entity.ExternalUser;
import com.cobnet.spring.boot.entity.PersistentLogins;
import com.cobnet.spring.boot.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.stereotype.Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class HttpAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        if(authentication instanceof AccountAuthenticationToken token) {

            token.setDetails(new WebAuthenticationDetails(request));

            System.out.println(ProjectBeanHolder.getRedisIndexedSessionRepository().findByPrincipalName(token.getPrincipal().getUsername()));
            //test to bind user
            if(token.getPrincipal() instanceof ExternalUser account) {

                if(account.getUser() == null) {

                    User user = ProjectBeanHolder.getUserRepository().findByUsernameEqualsIgnoreCase("admin").orElse(null);

                    if(user != null) {

                        account.setUser(user);

                        ProjectBeanHolder.getUserRepository().save(user);

                        ProjectBeanHolder.getExternalUserRepository().save(account);
                    }
                }
            }
        }

        System.out.println("successful logged in：" + authentication);

        if(authentication.getPrincipal() instanceof User user) {

            System.out.println("binding accounts：" + String.join(",", user.getExternalUsers().stream().map(ExternalUser::getUsername).toList()));
        }

        HttpSession session = request.getSession();

        ConnectionToken connectionToken = null;

        RememberMeInfo rememberMe = null;

        if(authentication.getPrincipal() instanceof User user) {

            if(user.getRemeberMeInfo() != null) {

                rememberMe = new RememberMeInfo(user.getRemeberMeInfo());
            }
        }

        if(session != null) {

            connectionToken = (ConnectionToken) session.getAttribute(ConnectionToken.ATTRIBUTE_KEY);

            if(connectionToken == null) {

                connectionToken = ConnectionToken.generate();
            }

            session.setAttribute(ConnectionToken.ATTRIBUTE_KEY, connectionToken);
        }

        String url = HttpRequestUrlResolver.getSuccessRedirectUrl(request);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if(url == null) {

            try (PrintWriter writer = response.getWriter()) {

                writer.write(ProjectBeanHolder.getObjectMapper().writeValueAsString(new AuthenticationResult(true, connectionToken, rememberMe)));
                writer.flush();
            }

        } else {

            ProjectBeanHolder.getRedirectStrategy().sendRedirect(request, response, url);
        }
    }
}
