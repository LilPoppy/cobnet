package com.cobnet.spring.boot.core;

import com.cobnet.spring.boot.configuration.SecurityConfiguration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HttpRequestUrlResolver {

    public static String getSuccessRedirectUrl(HttpServletRequest request) {

        return getResolvedUrl(request, ProjectBeanHolder.getSecurityConfiguration().getLoginSuccessUrl());
    }

    public static String getFailureRedirectUrl(HttpServletRequest request) {

        return  getResolvedUrl(request, request.getRequestURI().startsWith(ProjectBeanHolder.getSecurityConfiguration().getOauth2().getRedirectUrl()) ? ProjectBeanHolder.getSecurityConfiguration().getOauth2().getLoginFailureUrl() : ProjectBeanHolder.getSecurityConfiguration().getLoginFailureUrl());
    }

    public static String getResolvedUrl(HttpServletRequest request, String url) {

        HttpSession session = request.getSession();

        if(url != null && url.length() > 0) {

            switch (url.toUpperCase()) {

                case "{" + SecurityConfiguration.PREVIOUS_URL + "}":

                    return session != null ? (String) session.getAttribute(SecurityConfiguration.PREVIOUS_URL) : url;
            }
        }

        return url;
    }
}
