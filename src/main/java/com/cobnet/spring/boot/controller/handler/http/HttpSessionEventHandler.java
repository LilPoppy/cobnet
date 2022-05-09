package com.cobnet.spring.boot.controller.handler.http;

import com.cobnet.common.DateUtils;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.service.support.*;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
public class HttpSessionEventHandler extends HttpSessionEventPublisher {

    @Override
    public void sessionCreated(HttpSessionEvent event) {

        super.sessionCreated(event);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {

        super.sessionDestroyed(event);

        ProjectBeanHolder.getCacheService().evictIfPresent(AutocompleteRequestCache.GoogleMapServiceKey, AutocompleteRequestCache.class, event.getSession().getId());
        ProjectBeanHolder.getCacheService().evictIfPresent(HumanValidationCache.HumanValidatorKey, HumanValidationCache.class, event.getSession().getId());
        ProjectBeanHolder.getCacheService().evictIfPresent(MessageCallsCache.SecurityServiceKey, MessageCallsCache.class, event.getSession().getId());
        ProjectBeanHolder.getCacheService().evictIfPresent(BadMessageCache.SecurityServiceKey, BadMessageCache.class, event.getSession().getId());
        ProjectBeanHolder.getCacheService().evictIfPresent(AttemptLoginCache.AccountServiceName, AttemptLoginCache.class, event.getSession().getId());
    }

    @Override
    public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {

        super.sessionIdChanged(event, oldSessionId);

        ProjectBeanHolder.getCacheService().transfer(AutocompleteRequestCache.GoogleMapServiceKey, AutocompleteRequestCache.class, oldSessionId, event.getSession().getId());
        ProjectBeanHolder.getCacheService().transfer(HumanValidationCache.HumanValidatorKey, HumanValidationCache.class, oldSessionId, event.getSession().getId());
        ProjectBeanHolder.getCacheService().transfer(MessageCallsCache.SecurityServiceKey, MessageCallsCache.class, oldSessionId, event.getSession().getId());
        ProjectBeanHolder.getCacheService().transfer(BadMessageCache.SecurityServiceKey, BadMessageCache.class, oldSessionId, event.getSession().getId());
        ProjectBeanHolder.getCacheService().transfer(AttemptLoginCache.AccountServiceName, AttemptLoginCache.class, oldSessionId, event.getSession().getId());
    }
}
