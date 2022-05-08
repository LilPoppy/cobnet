package com.cobnet.spring.boot.controller.handler.http;

import com.cobnet.common.DateUtils;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.service.support.AutocompleteRequestCache;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSessionEvent;
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
    }

    @Override
    public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {

        super.sessionIdChanged(event, oldSessionId);

        Set<String> keys = ProjectBeanHolder.getCacheService().keys(AutocompleteRequestCache.GoogleMapServiceKey);

        for(String key : keys) {

            if(key.equals(oldSessionId)) {

                AutocompleteRequestCache cache = ProjectBeanHolder.getGoogleMapService().getAutocompleteRequestCache(oldSessionId);

                if(cache != null) {

                    ProjectBeanHolder.getCacheService().evictIfPresent(AutocompleteRequestCache.GoogleMapServiceKey, oldSessionId);
                    ProjectBeanHolder.getCacheService().set(AutocompleteRequestCache.GoogleMapServiceKey, event.getSession().getId(), cache, ProjectBeanHolder.getSecurityConfiguration().getGoogleMapAutoCompleteLimitDuration().minus(DateUtils.getInterval(DateUtils.now(), cache.creationTime())));
                    break;
                }
            }
        }
    }

}
