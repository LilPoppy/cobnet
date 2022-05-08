package com.cobnet.spring.boot.service;

import com.cobnet.common.DateUtils;
import com.cobnet.exception.ResponseFailureStatusException;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.support.SecurityRequestStatus;
import com.cobnet.spring.boot.service.support.BadMessageCache;
import com.cobnet.spring.boot.service.support.MessageCallsCache;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Null;
import java.time.Duration;
import java.util.Date;

@Service
public class SecurityService {

    public BadMessageCache getBadMessageCache(HttpServletRequest request) {

        return ProjectBeanHolder.getCacheService().get(BadMessageCache.SecurityServiceKey, BadMessageCache.class, request.getSession(true).getId());
    }

    public MessageCallsCache getMessageCallsCache(HttpServletRequest request) {

        return ProjectBeanHolder.getCacheService().get(BadMessageCache.SecurityServiceKey, MessageCallsCache.class, request.getSession(true).getId());
    }

    public BadMessageCache addBadMessage(HttpServletRequest request) {

        HttpSession session = request.getSession(true);
        HttpMethod method = HttpMethod.resolve(request.getMethod());
        String path = request.getRequestURI();

        BadMessageCache cache = getBadMessageCache(request);

        if(cache == null) {

            cache = new BadMessageCache(method, path);

        } else {

            cache = (BadMessageCache) cache.add(method, path);
        }

        ProjectBeanHolder.getCacheService().set(BadMessageCache.SecurityServiceKey, session.getId(), cache, Duration.ofDays(1));

        return cache;
    }

    public MessageCallsCache addMessageCalls(HttpServletRequest request) {

        HttpSession session = request.getSession(true);
        HttpMethod method = HttpMethod.resolve(request.getMethod());
        String path = request.getRequestURI();

        MessageCallsCache cache = getMessageCallsCache(request);

        if(cache == null) {

            cache = new MessageCallsCache(method, path);

        } else {

            cache = (MessageCallsCache) cache.add(method, path);
        }

        ProjectBeanHolder.getCacheService().set(MessageCallsCache.SecurityServiceKey, session.getId(), cache, Duration.ofDays(1));

        return cache;
    }

    public boolean isSessionAuthorized(@Nullable HttpServletRequest request, @Nullable MessageCallsCache cache) {

        if(request == null) {

            request = ProjectBeanHolder.getCurrentHttpRequest();
        }

        if(cache == null) {

            cache = this.getMessageCallsCache(request);
        }

        if(ProjectBeanHolder.getSecurityConfiguration().isSessionLimitEnable() && cache.count() >= ProjectBeanHolder.getSecurityConfiguration().getSessionBeforeCreatedTimeAllowRequestCount()) {

            HttpSession session = request.getSession(true);

            if(ProjectBeanHolder.getSecurityConfiguration().getSessionCreatedTimeRequire().compareTo(DateUtils.getInterval(new Date(session.getCreationTime()), DateUtils.now())) > 0) {

                return false;
            }
        }

        return true;
    }
}
