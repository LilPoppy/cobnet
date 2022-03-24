package com.cobnet.spring.boot.controller.handler.http;

import com.cobnet.security.AccountAuthenticationToken;
import com.cobnet.spring.boot.configuration.SecurityConfiguration;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.ExternalUser;
import com.cobnet.spring.boot.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
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

        System.out.println("successful logged in：" + authentication);

        if(authentication.getPrincipal() instanceof User user) {


            System.out.println("binding accounts：" + String.join(",", user.getExternalUsers().stream().map(ExternalUser::getUsername).toList()));
        }

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
