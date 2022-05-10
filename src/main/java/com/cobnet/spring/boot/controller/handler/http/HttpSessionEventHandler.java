package com.cobnet.spring.boot.controller.handler.http;

import com.cobnet.spring.boot.configuration.SessionConfiguration;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.service.RedisSessionService;
import com.cobnet.spring.boot.service.support.*;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

@Component
public class HttpSessionEventHandler extends HttpSessionEventPublisher {

    @Override
    public void sessionCreated(HttpSessionEvent event) {

        System.out.println(event.getSession().getId() + " is creating!");
        super.sessionCreated(event);

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {

        super.sessionDestroyed(event);

        //TODO distributed management systems
        System.out.println(event.getSession().getId() + " is destroying!");
        ProjectBeanHolder.getCacheService().evictIfPresent(AutocompleteRequestCache.GoogleMapServiceKey, AutocompleteRequestCache.class, event.getSession().getId());
        ProjectBeanHolder.getCacheService().evictIfPresent(HumanValidationCache.HumanValidatorKey, HumanValidationCache.class, event.getSession().getId());
        ProjectBeanHolder.getCacheService().evictIfPresent(MessageCallsCache.SecurityServiceKey, MessageCallsCache.class, event.getSession().getId());
        ProjectBeanHolder.getCacheService().evictIfPresent(BadMessageCache.SecurityServiceKey, BadMessageCache.class, event.getSession().getId());
        ProjectBeanHolder.getCacheService().evictIfPresent(AttemptLoginCache.AccountServiceName, AttemptLoginCache.class, event.getSession().getId());

        ProjectBeanHolder.getRedisSessionService().remove(RedisSessionService.IP_ADDRESS_INDEX_NAME, (String) event.getSession().getAttribute(SessionConfiguration.IP_ADDRESS), event.getSession().getId());
    }

    @Override
    public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {

        super.sessionIdChanged(event, oldSessionId);

        onChangeSessionId(event.getSession(), oldSessionId, event.getSession().getId());

    }

    public static void onChangeSessionId(HttpSession session, String oldSessionId, String newSessionId) {

        ProjectBeanHolder.getCacheService().transfer(AutocompleteRequestCache.GoogleMapServiceKey, AutocompleteRequestCache.class, oldSessionId, newSessionId);
        ProjectBeanHolder.getCacheService().transfer(HumanValidationCache.HumanValidatorKey, HumanValidationCache.class, oldSessionId, newSessionId);
        ProjectBeanHolder.getCacheService().transfer(MessageCallsCache.SecurityServiceKey, MessageCallsCache.class, oldSessionId, newSessionId);
        ProjectBeanHolder.getCacheService().transfer(BadMessageCache.SecurityServiceKey, BadMessageCache.class, oldSessionId, newSessionId);
        ProjectBeanHolder.getCacheService().transfer(AttemptLoginCache.AccountServiceName, AttemptLoginCache.class, oldSessionId, newSessionId);

        String ipAddress = (String) session.getAttribute(SessionConfiguration.IP_ADDRESS);
        ProjectBeanHolder.getRedisSessionService().remove(RedisSessionService.IP_ADDRESS_INDEX_NAME, ipAddress, oldSessionId);
        ProjectBeanHolder.getRedisSessionService().add(RedisSessionService.IP_ADDRESS_INDEX_NAME, ipAddress, newSessionId);
    }
}
