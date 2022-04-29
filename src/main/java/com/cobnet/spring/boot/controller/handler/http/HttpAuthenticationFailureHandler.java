package com.cobnet.spring.boot.controller.handler.http;

import com.cobnet.common.DateUtils;
import com.cobnet.exception.AuthenticationCancelledException;
import com.cobnet.exception.AuthenticationSecurityException;
import com.cobnet.spring.boot.core.HttpRequestUrlResolver;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.CommentWrapper;
import com.cobnet.spring.boot.dto.ObjectWrapper;
import com.cobnet.spring.boot.dto.ResponseResult;
import com.cobnet.spring.boot.dto.support.AuthenticationStatus;
import com.cobnet.spring.boot.entity.User;
import com.cobnet.spring.boot.service.support.AttemptLoginCache;
import org.springframework.beans.factory.annotation.Autowired;
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

        System.out.println(exception);

        if(url == null) {

            try (PrintWriter writer = response.getWriter()) {

                if(exception instanceof UsernameNotFoundException || exception instanceof DisabledException) {

                    response.setStatus(AuthenticationStatus.USER_NOT_FOUND.getCode());

                    String key = request.getSession(true).getId();

                    AttemptLoginCache cache = ProjectBeanHolder.getCacheService().get(AttemptLoginCache.AccountServiceName, key, AttemptLoginCache.class);

                    if(cache == null) {

                        cache = new AttemptLoginCache(DateUtils.now(), 0);

                        ProjectBeanHolder.getCacheService().set(AttemptLoginCache.AccountServiceName, key, cache, ProjectBeanHolder.getSecurityConfiguration().getMaxAttemptLoginReset());

                    } else {

                        cache = new AttemptLoginCache(cache.createdTime(), cache.times() + 1);

                        ProjectBeanHolder.getCacheService().set(AttemptLoginCache.AccountServiceName, key, cache, ProjectBeanHolder.getSecurityConfiguration().getMaxAttemptLoginReset().minus(DateUtils.getInterval(DateUtils.now(), cache.createdTime())));
                    }

                    if(cache.times() >= ProjectBeanHolder.getSecurityConfiguration().getMaxAttemptLogin()) {

                        writer.write(ProjectBeanHolder.getObjectMapper().writeValueAsString(new ResponseResult<>(AuthenticationStatus.REACHED_MAXIMUM_ATTEMPT)));

                    } else {

                        writer.write(ProjectBeanHolder.getObjectMapper().writeValueAsString(new ResponseResult(AuthenticationStatus.PASSWORD_NOT_MATCH, new CommentWrapper<>("Login attempt time left.", new ObjectWrapper<>(ProjectBeanHolder.getSecurityConfiguration().getMaxAttemptLogin() - ProjectBeanHolder.getCacheService().get(AttemptLoginCache.AccountServiceName, request.getSession().getId(), AttemptLoginCache.class).times())))));
                    }

                } else if(exception instanceof BadCredentialsException) {

                    response.setStatus(AuthenticationStatus.PASSWORD_NOT_MATCH.getCode());
                    writer.write(ProjectBeanHolder.getObjectMapper().writeValueAsString(new ResponseResult(AuthenticationStatus.PASSWORD_NOT_MATCH, new CommentWrapper<>("Login attempt time left.", new ObjectWrapper<>(ProjectBeanHolder.getSecurityConfiguration().getMaxAttemptLogin() - ProjectBeanHolder.getCacheService().get(AttemptLoginCache.AccountServiceName, request.getSession().getId(), AttemptLoginCache.class).times())))));

                } else if(exception instanceof AuthenticationCancelledException) {

                    response.setStatus(AuthenticationStatus.REJECTED.getCode());
                    writer.write(ProjectBeanHolder.getObjectMapper().writeValueAsString(new ResponseResult<>(AuthenticationStatus.REJECTED)));

                } else if(exception instanceof LockedException) {

                    String username = request.getParameter(ProjectBeanHolder.getSecurityConfiguration().getUsernameParameter());

                    if(userDetailsService.loadUserByUsername(username) instanceof User user) {

                        response.setStatus(AuthenticationStatus.LOCKED.getCode());
                        writer.write(ProjectBeanHolder.getObjectMapper().writeValueAsString(new ResponseResult<>(AuthenticationStatus.LOCKED, new CommentWrapper<>("", new ObjectWrapper<>(user.getLockTime())))));
                    }

                } else if(exception instanceof AuthenticationSecurityException ex) {

                    response.setStatus(ex.getStatus().getCode());

                    switch (ex.getStatus()) {

                        case HUMAN_VALIDATION_REQUEST, REACHED_MAXIMUM_ATTEMPT, REJECTED -> writer.write(ProjectBeanHolder.getObjectMapper().writeValueAsString(new ResponseResult<>(ex.getStatus())));
                    }
                }

                writer.flush();
            }

        } else {

            ProjectBeanHolder.getRedirectStrategy().sendRedirect(request, response, url);
        }
    }
}
