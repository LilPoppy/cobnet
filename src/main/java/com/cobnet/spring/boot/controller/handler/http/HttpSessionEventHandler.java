package com.cobnet.spring.boot.controller.handler.http;

import com.cobnet.interfaces.spring.repository.IndexedRedisRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionEvent;

@Component
public class HttpSessionEventHandler extends HttpSessionEventPublisher {

    @Override
    public void sessionCreated(HttpSessionEvent event) {

        super.sessionCreated(event);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {

        super.sessionDestroyed(event);

        //TODO distributed management systems
        IndexedRedisRepository.deleteAll(event.getSession());
    }

    @Override
    public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {

        super.sessionIdChanged(event, oldSessionId);

        IndexedRedisRepository.updateIndex(oldSessionId, event.getSession());
    }
}
