package com.cobnet.spring.boot.controller.handler.http;

import com.cobnet.common.DateUtils;
import com.cobnet.exception.AuthenticationCancelledException;
import com.cobnet.exception.AuthenticationSecurityException;
import com.cobnet.spring.boot.core.HttpRequestUrlResolver;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.cobnet.exception.support.AuthenticationStatus;
import com.cobnet.spring.boot.entity.User;
import com.cobnet.spring.boot.cache.AttemptLoginCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class HttpAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String url = HttpRequestUrlResolver.getFailureRedirectUrl(request);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if(url == null) {

            try (PrintWriter writer = response.getWriter()) {

                if(exception instanceof UsernameNotFoundException || exception instanceof DisabledException) {

                    response.setStatus(AuthenticationStatus.USER_NOT_FOUND.getCode());

                    String key = request.getSession(true).getId();

                    AttemptLoginCache cache =  ProjectBeanHolder.getSecurityService().getAttemptLoginCache(key);

                    if(cache == null) {

                        cache = new AttemptLoginCache(key, DateUtils.now(), 0);

                        ProjectBeanHolder.getSecurityService().setAttemptLoginCache(cache, ProjectBeanHolder.getSecurityConfiguration().getMaxAttemptLoginReset());

                    } else {

                        cache = cache.setCount(cache.getCount() + 1);

                        ProjectBeanHolder.getSecurityService().setAttemptLoginCache(cache, ProjectBeanHolder.getSecurityConfiguration().getMaxAttemptLoginReset().minus(DateUtils.getInterval(DateUtils.now(), cache.getCreationTime())));
                    }

                    if(cache.getCount() >= ProjectBeanHolder.getSecurityConfiguration().getMaxAttemptLogin()) {

                        writer.write(ProjectBeanHolder.getObjectMapper().writeValueAsString(new ResponseResult<>(AuthenticationStatus.REACHED_MAXIMUM_ATTEMPT)));

                    } else {

                        writer.write(ProjectBeanHolder.getObjectMapper().writeValueAsString(new ResponseResult(AuthenticationStatus.PASSWORD_NOT_MATCH, new CommentWrapper<>("Login attempt time left.", new MessageWrapper("attempt-remain", ProjectBeanHolder.getSecurityConfiguration().getMaxAttemptLogin() - cache.getCount())))));
                    }

                } else if(exception instanceof BadCredentialsException) {

                    response.setStatus(AuthenticationStatus.PASSWORD_NOT_MATCH.getCode());
                    writer.write(ProjectBeanHolder.getObjectMapper().writeValueAsString(new ResponseResult(AuthenticationStatus.PASSWORD_NOT_MATCH, new CommentWrapper<>("Login attempt time left.", new MessageWrapper("attempt-remain",ProjectBeanHolder.getSecurityConfiguration().getMaxAttemptLogin() - ProjectBeanHolder.getSecurityService().getAttemptLoginCache(request.getSession().getId()).getCount())))));

                } else if(exception instanceof AuthenticationCancelledException) {

                    response.setStatus(AuthenticationStatus.REJECTED.getCode());
                    writer.write(ProjectBeanHolder.getObjectMapper().writeValueAsString(new ResponseResult<>(AuthenticationStatus.REJECTED)));

                } else if(exception instanceof LockedException) {

                    String username = request.getParameter(ProjectBeanHolder.getSecurityConfiguration().getUsernameParameter());

                    if(userDetailsService.loadUserByUsername(username) instanceof User user) {

                        response.setStatus(AuthenticationStatus.LOCKED.getCode());
                        writer.write(ProjectBeanHolder.getObjectMapper().writeValueAsString(new ResponseResult<>(AuthenticationStatus.LOCKED,  new MessageWrapper("lock-until", user.getLockTime()))));
                    }

                } else if(exception instanceof AuthenticationSecurityException ex) {

                    response.setStatus(ex.getStatus().getCode());

                    switch (ex.getStatus()) {
                        case HUMAN_VALIDATION_REQUIRED -> writer.write(ProjectBeanHolder.getObjectMapper().writeValueAsString(new ResponseResult<>(ex.getStatus(), new CommentWrapper<>("Human validation required.", new MethodHint(HttpMethod.PATCH, "/visitor/human-validate")))));
                        case REACHED_MAXIMUM_ATTEMPT, REJECTED -> writer.write(ProjectBeanHolder.getObjectMapper().writeValueAsString(new ResponseResult<>(ex.getStatus())));
                    }
                }

                writer.flush();
            }

        } else {

            response.setStatus(HttpStatus.FOUND.value());

            if(ProjectBeanHolder.getSecurityConfiguration().isLoginFailureRedirectUseXForwardedPrefix()) {

                ProjectBeanHolder.getRedirectStrategy().sendRedirect(request, response, url);

            } else {

                response.sendRedirect(url);
            }
        }
    }
}
