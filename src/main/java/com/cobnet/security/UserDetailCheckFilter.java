package com.cobnet.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserDetailCheckFilter extends GenericFilterBean {

    private final RequestMatcher matcher;

    @Autowired
    private UserDetailsService userDetailsService;

    public UserDetailCheckFilter(String url) {

        this.matcher = new AntPathRequestMatcher(url);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if(request instanceof HttpServletRequest httpRequest) {

            if (this.matcher.matches(httpRequest) && httpRequest.getMethod().equalsIgnoreCase("post")) {

                if(response instanceof HttpServletResponse httpResponse) {

                    if(this.userDetailsService == null) {

                        //this.userDetailsService.loadUserByUsername()
                        httpResponse.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
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
