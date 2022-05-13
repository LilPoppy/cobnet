package com.cobnet.spring.boot.service;

import com.cobnet.common.DateUtils;
import com.cobnet.interfaces.spring.repository.*;
import com.cobnet.spring.boot.cache.AttemptLoginCache;
import com.cobnet.spring.boot.cache.IPAddressCache;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.cache.BadMessageCache;
import com.cobnet.spring.boot.cache.MessageCallsCache;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SecurityService {

    @Autowired
    private AttemptLoginCacheRepository attemptLoginCacheRepository;

    @Autowired
    private BadMessageCacheRepository badMessageCacheRepository;

    @Autowired
    private MessageCallsCacheRepository messageCallsCacheRepository;

    @Autowired
    private IPAddressCacheRepository ipAddressCacheRepository;

    public List<IPAddressCache> getIPAddressCaches(String address) {

        return ipAddressCacheRepository.findByIpAddressEquals(address);
    }

    public IPAddressCache getIPAddressCache(String key) {

        Optional<IPAddressCache> optional = ipAddressCacheRepository.findById(key);

        if(optional.isEmpty()) {

            return null;
        }

        return optional.get();
    }

    public IPAddressCache setIPAddressCache(IPAddressCache cache) {

        return setIPAddressCache(cache, null);
    }

    public IPAddressCache setIPAddressCache(IPAddressCache cache, Duration expiration) {

        if(expiration != null && !expiration.isZero()) {

            return ipAddressCacheRepository.save(cache, expiration);
        }

        return ipAddressCacheRepository.save(cache);
    }

    public AttemptLoginCache getAttemptLoginCache(String key) {

        Optional<AttemptLoginCache> optional = attemptLoginCacheRepository.findById(key);

        if(optional.isEmpty()) {

            return null;
        }

        return optional.get();
    }

    public AttemptLoginCache setAttemptLoginCache(AttemptLoginCache cache) {

        return setAttemptLoginCache(cache, null);
    }

    public AttemptLoginCache setAttemptLoginCache(AttemptLoginCache cache, Duration expiration) {

        if(expiration != null && !expiration.isZero()) {

            return attemptLoginCacheRepository.save(cache, expiration);
        }

        return attemptLoginCacheRepository.save(cache);
    }


    public BadMessageCache getBadMessageCache(HttpServletRequest request) {

        Optional<BadMessageCache> optional = badMessageCacheRepository.findById(request.getSession(true).getId());

        if(optional.isEmpty()) {

            return null;
        }

        return optional.get();
    }

    public MessageCallsCache getMessageCallsCache(HttpServletRequest request) {

        Optional<MessageCallsCache> optional = messageCallsCacheRepository.findById(request.getSession(true).getId());

        if(optional.isEmpty()) {

            return null;
        }

        return optional.get();
    }


    public BadMessageCache addBadMessage(HttpServletRequest request) {

        HttpSession session = request.getSession(true);
        HttpMethod method = HttpMethod.resolve(request.getMethod());
        String path = request.getRequestURI();

        BadMessageCache cache = getBadMessageCache(request);

        if(cache == null) {

            cache = new BadMessageCache(session.getId(), method, path);

        } else {

            cache = (BadMessageCache) cache.add(method, path);
        }

        badMessageCacheRepository.save(cache, ProjectBeanHolder.getSecurityConfiguration().getSession().getBadMessageLogCacheExpire());

        return cache;
    }

    public MessageCallsCache addMessageCalls(HttpServletRequest request) {

        HttpSession session = request.getSession(true);
        HttpMethod method = HttpMethod.resolve(request.getMethod());
        String path = request.getRequestURI();

        MessageCallsCache cache = getMessageCallsCache(request);

        if(cache == null) {

            cache = new MessageCallsCache(session.getId(), method, path);

        } else {

            cache = (MessageCallsCache) cache.add(method, path);
        }

        messageCallsCacheRepository.save(cache, ProjectBeanHolder.getSecurityConfiguration().getSession().getMessageLogCacheExpire());

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

            if(cache.getCount() >= ProjectBeanHolder.getSecurityConfiguration().getSession().getBeforeCreatedTimeMaxMessageCount()) {

                return false;
            }

        }

        return true;
    }
}
