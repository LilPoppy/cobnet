package com.cobnet.spring.boot.service;

import com.cobnet.common.DateUtils;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.service.support.BadMessageCache;
import com.cobnet.spring.boot.service.support.MessageCallsCache;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

        ProjectBeanHolder.getCacheService().set(BadMessageCache.SecurityServiceKey, session.getId(), cache, ProjectBeanHolder.getSecurityConfiguration().getSession().getBadMessageLogCacheExpire());

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

        ProjectBeanHolder.getCacheService().set(MessageCallsCache.SecurityServiceKey, session.getId(), cache, ProjectBeanHolder.getSecurityConfiguration().getSession().getMessageLogCacheExpire());

        return cache;
    }

    public boolean isSessionAuthorized(@Nullable HttpServletRequest request, @Nullable MessageCallsCache cache) {

        if(request == null) {

            request = ProjectBeanHolder.getCurrentHttpRequest();
        }

        if(cache == null) {

            cache = this.getMessageCallsCache(request);
        }

        if(cache == null) {

            return true;
        }

        HttpSession session = request.getSession(true);

        if(ProjectBeanHolder.getSecurityConfiguration().getSession().isEnable() && ProjectBeanHolder.getSecurityConfiguration().getSession().getBypassCheckAfter().compareTo(DateUtils.getInterval(new Date(session.getCreationTime()), DateUtils.now())) > 0) {

            if(cache.count() >= ProjectBeanHolder.getSecurityConfiguration().getSession().getBeforeCreatedTimeMaxMessageCount()) {

                return false;
            }

        }

        return true;
    }
}
