package com.cobnet.security;

import com.cobnet.exception.ResponseFailureStatusException;
import com.cobnet.interfaces.security.annotation.HumanValidationRequired;
import com.cobnet.spring.boot.cache.IPAddressCache;
import com.cobnet.spring.boot.controller.handler.http.HttpControllerExceptionHandler;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.support.SecurityRequestStatus;
import com.cobnet.spring.boot.cache.BadMessageCache;
import com.cobnet.spring.boot.cache.MessageCallsCache;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SecurityHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        try {

            if(!request.getRequestURI().equalsIgnoreCase("/error")) {

                HttpSession session = request.getSession(true);

                ProjectBeanHolder.getSecurityService().setIPAddressCache(new IPAddressCache(session.getId(), request.getRemoteAddr()));

                MessageCallsCache cache = null;

                if(ProjectBeanHolder.getSecurityConfiguration().getSession().isMessageLogCacheEnable()) {

                    cache = ProjectBeanHolder.getSecurityService().addMessageCalls(request);
                }

                ProjectBeanHolder.getSecurityService().securityCheck(request, cache);;

                if(handler instanceof HandlerMethod method) {

                    HumanValidationRequired required = method.getMethodAnnotation(HumanValidationRequired.class);

                    if (required != null && required.enabled() && ProjectBeanHolder.getSecurityConfiguration().getHumanValidation().isEnable() && !ProjectBeanHolder.getHumanValidator().isValidated(session.getId())) {

                        throw new ResponseFailureStatusException(SecurityRequestStatus.SECURITY_FORBIDDEN_HUMAN_VALIDATION);
                    }
                }

            } else {

                if(ProjectBeanHolder.getSecurityConfiguration().getSession().isBadMessageLogCacheEnable()) {

                    BadMessageCache cache = ProjectBeanHolder.getSecurityService().addBadMessage(request);

                    if(ProjectBeanHolder.getSecurityConfiguration().getSession().getMaxErrorMessage() > cache.getCount()) {

                        //TODO reject the service to current session
                    }
                }
            }

        } catch (ResponseFailureStatusException ex) {

            ProjectBeanHolder.getSpringContext().getBean(HttpControllerExceptionHandler.class).handleResponseFailureStatusException(request, response, ex);

            return false;
        }

        return true;
    }
}
