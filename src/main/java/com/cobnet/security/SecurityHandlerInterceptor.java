package com.cobnet.security;

import com.cobnet.exception.ResponseFailureStatusException;
import com.cobnet.interfaces.security.annotation.HumanValidationRequired;
import com.cobnet.spring.boot.configuration.SecurityConfiguration;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.support.GoogleApiRequestResultStatus;
import com.cobnet.spring.boot.dto.support.SecurityRequestStatus;
import com.cobnet.spring.boot.service.support.BadMessageCache;
import com.cobnet.spring.boot.service.support.MessageCallsCache;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
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

            System.out.println(ProjectBeanHolder.getRedisSessionService().getIndexKeys(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME));
            MessageCallsCache cache = null;

            if(ProjectBeanHolder.getSecurityConfiguration().getSession().isMessageLogCacheEnable()) {

                 cache = ProjectBeanHolder.getSecurityService().addMessageCalls(request);
            }

            if(!ProjectBeanHolder.getSecurityService().isSessionAuthorized(request, cache)) {

                throw new ResponseFailureStatusException(SecurityRequestStatus.SECURITY_FORBIDDEN_SESSION);
            }

            if(handler instanceof HandlerMethod method) {

                HumanValidationRequired required = method.getMethodAnnotation(HumanValidationRequired.class);

                if (required != null && required.enabled() && ProjectBeanHolder.getSecurityConfiguration().getHumanValidation().isEnable() && !ProjectBeanHolder.getHumanValidator().isValidated(session.getId())) {

                    throw new ResponseFailureStatusException(SecurityRequestStatus.SECURITY_FORBIDDEN_HUMAN_VALIDATION);
                }
            }

        } else {

            if(ProjectBeanHolder.getSecurityConfiguration().getSession().isBadMessageLogCacheEnable()) {

                BadMessageCache cache = ProjectBeanHolder.getSecurityService().addBadMessage(request);

                if(ProjectBeanHolder.getSecurityConfiguration().getSession().getMaxErrorMessage() > cache.count()) {

                    //TODO reject the service to current session
                }
            }
        }

        return true;
    }
}
