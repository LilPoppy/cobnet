package com.cobnet.connection.handler.spring;

import com.cobnet.spring.boot.configuration.SecurityConfiguration;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        HttpSession session = request.getSession();


        System.out.println("success login " + authentication);

        System.out.println(ProjectBeanHolder.getSessionRegistry().getAllSessions(authentication.getPrincipal(), true));

        if(session != null) {

            //TODO generate Netty connection token

            String url = ProjectBeanHolder.getSecurityConfiguration().getLoginSuccessUrl();

            if(url.equalsIgnoreCase("{PREVIOUS_URL}")) {

                url = (String) session.getAttribute(SecurityConfiguration.PREVIOUS_URL);
            }

            if(url != null) {

                ProjectBeanHolder.getRedirectStrategy().sendRedirect(request, response, url);
            }
        }
    }
}
