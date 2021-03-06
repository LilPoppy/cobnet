package com.cobnet.security;

import com.cobnet.common.DateUtils;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class UserLockStatusFilter extends GenericFilterBean {

    private final RequestMatcher matcher;

    @Autowired
    private UserDetailsService userDetailsService;

    public UserLockStatusFilter(String url) {

        this.matcher = new AntPathRequestMatcher(url);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if(request instanceof HttpServletRequest httpRequest) {

            if (this.matcher.matches(httpRequest) && httpRequest.getMethod().equalsIgnoreCase("post")) {

                String username = request.getParameter(ProjectBeanHolder.getSecurityConfiguration().getUsernameParameter());

                if(username != null) {

                    if(userDetailsService.loadUserByUsername(username) instanceof User user) {

                        if(user.getLockTime() != null && user.getLockTime().before(DateUtils.now())) {

                            user.setLocked(false);
                            user.setLockTime(null);
                            ProjectBeanHolder.getUserRepository().save(user);
                        }
                    }
                }

            } else if (this.logger.isTraceEnabled()) {

                this.logger.trace(LogMessage.format("Did not match request to %s", this.matcher));
            }

        } else {

            this.logger.trace(LogMessage.format("%s did not match request to %s", HttpServletRequest.class.getName(), request.getClass().getName()));
        }

        chain.doFilter(request, response);
    }
}
