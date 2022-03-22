package com.cobnet.connection.handler.http;

import com.cobnet.security.AccountAuthenticationToken;
import com.cobnet.spring.boot.configuration.SecurityConfiguration;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.ExternalUser;
import com.cobnet.spring.boot.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

public class HttpAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        if(authentication instanceof AccountAuthenticationToken token) {

            token.setDetails(new WebAuthenticationDetails(request));

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

        HttpSession session = request.getSession();

        System.out.println("success login " + authentication);

        System.out.println(ProjectBeanHolder.getSessionRegistry().getAllSessions(authentication.getPrincipal(), true).stream().map(registry -> registry.getSessionId()).toList());

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
