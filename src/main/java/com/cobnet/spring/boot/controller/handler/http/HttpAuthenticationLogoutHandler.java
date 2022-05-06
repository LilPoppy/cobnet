package com.cobnet.spring.boot.controller.handler.http;

import com.cobnet.spring.boot.core.HttpRequestUrlResolver;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class HttpAuthenticationLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String url = HttpRequestUrlResolver.getLogoutRedirectUrl(request);

        SessionInformation information = ProjectBeanHolder.getSessionRegistry().getSessionInformation(request.getSession().getId());

        if(information != null) {

            information.expireNow();
            ProjectBeanHolder.getSessionRegistry().removeSessionInformation(information.getSessionId());
        }

        if(url != null) {

            response.setStatus(HttpStatus.FOUND.value());

            try {

                if(ProjectBeanHolder.getSecurityConfiguration().isLogoutSuccessRedirectUseXForwardedPrefix()) {

                    ProjectBeanHolder.getRedirectStrategy().sendRedirect(request, response, url);

                } else {

                    response.sendRedirect(url);
                }

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }
}
