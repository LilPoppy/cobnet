package com.cobnet.security;

import com.cobnet.exception.ResponseFailureStatusException;
import com.cobnet.interfaces.security.annotation.HumanValidationRequired;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.support.GoogleApiRequestResultStatus;
import com.cobnet.spring.boot.dto.support.SecurityRequestStatus;
import com.cobnet.spring.boot.service.support.MessageCallsCache;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SecurityHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if(!request.getRequestURI().equalsIgnoreCase("/error")) {

            HttpSession session = request.getSession(true);

            MessageCallsCache cache = ProjectBeanHolder.getSecurityService().addMessageCalls(request);

            if(!ProjectBeanHolder.getSecurityService().isSessionAuthorized(request, cache)) {

                throw new ResponseFailureStatusException(SecurityRequestStatus.SESSION_FORBIDDEN);
            }

            if(handler instanceof HandlerMethod method) {

                HumanValidationRequired required = method.getMethodAnnotation(HumanValidationRequired.class);

                if (required != null && required.enabled() && ProjectBeanHolder.getSecurityConfiguration().isHumanValidationEnable() && !ProjectBeanHolder.getHumanValidator().isValidated(session.getId())) {

                    throw new ResponseFailureStatusException(SecurityRequestStatus.HUMAN_VALIDATION_REQUIRED);
                }
            }
        }

        return true;
    }
}
